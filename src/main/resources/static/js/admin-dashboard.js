/**
 * TRIX Workshops - Admin Dashboard Controller
 */

let categoryChartInstance = null;
let statusChartInstance = null;

document.addEventListener('DOMContentLoaded', async () => {
  const user = await Auth.init('ADMIN');
  if (!user) return;

  // Set current date display
  const dateEl = document.getElementById('current-date-display');
  if (dateEl) {
    dateEl.textContent = new Date().toLocaleDateString('en-US', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  // Pre-fill creation form default dates (tomorrow and tomorrow + 7 days)
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 7);
  const deadline = new Date();
  deadline.setDate(deadline.getDate() + 5);

  const dateInput = document.querySelector('input[name="workshopDate"]');
  const deadlineInput = document.querySelector('input[name="registrationDeadline"]');
  if (dateInput) dateInput.value = tomorrow.toISOString().split('T')[0];
  if (deadlineInput) deadlineInput.value = deadline.toISOString().split('T')[0];

  loadDashboardData();

  // Create Workshop form handler
  const form = document.getElementById('create-workshop-form');
  if (form) {
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const saveBtn = document.getElementById('save-workshop-btn');
      saveBtn.disabled = true;
      saveBtn.textContent = 'Publishing...';

      const formData = new FormData(form);
      const payload = {
        title: formData.get('title'),
        description: formData.get('description'),
        category: formData.get('category'),
        organizerName: formData.get('organizerName'),
        workshopDate: formData.get('workshopDate'),
        startTime: formData.get('startTime') + ':00',
        endTime: formData.get('endTime') + ':00',
        venue: formData.get('venue'),
        capacity: parseInt(formData.get('capacity'), 10),
        registrationDeadline: formData.get('registrationDeadline'),
        status: 'UPCOMING'
      };

      try {
        await API.post('/api/workshops', payload);
        showToast('Workshop published successfully!', 'success');
        closeModal('create-workshop-modal');
        form.reset();
        loadDashboardData();
      } catch (err) {
        showToast(err.message || 'Failed to create workshop', 'error');
      } finally {
        saveBtn.disabled = false;
        saveBtn.textContent = 'Publish Workshop';
      }
    });
  }
});

async function loadDashboardData() {
  try {
    const stats = await API.get('/api/dashboard/admin');

    // 1. Metric cards
    document.getElementById('stat-total-workshops').textContent = stats.totalWorkshops || 0;
    document.getElementById('stat-upcoming-workshops').textContent = stats.upcomingWorkshops || 0;
    document.getElementById('stat-total-participants').textContent = stats.totalParticipants || 0;
    document.getElementById('stat-pending-registrations').textContent = stats.pendingRegistrations || 0;
    document.getElementById('stat-attendance-rate').textContent = (stats.overallAttendanceRate || 0) + '%';
    document.getElementById('stat-present-count').textContent = stats.totalPresent || 0;
    document.getElementById('stat-absent-count').textContent = stats.totalAbsent || 0;
    document.getElementById('stat-certificates-issued').textContent = stats.certificatesIssued || 0;

    // 2. Charts
    renderCategoryChart(stats.categoryBreakdown || {});
    renderStatusChart(stats.registrationStatusBreakdown || {});

    // 3. Upcoming workshops table
    renderUpcomingWorkshops(stats.upcomingWorkshopsList || []);

    // 4. Activities feed
    renderRecentActivities(stats.recentActivities || []);

  } catch (err) {
    showToast('Failed to load dashboard statistics: ' + err.message, 'error');
  }
}

function renderCategoryChart(categoryMap) {
  const ctx = document.getElementById('categoryChart');
  if (!ctx) return;

  const labels = Object.keys(categoryMap);
  const data = Object.values(categoryMap);

  if (categoryChartInstance) {
    categoryChartInstance.destroy();
  }

  categoryChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels.length ? labels : ['No Data'],
      datasets: [{
        label: 'Workshops',
        data: data.length ? data : [0],
        backgroundColor: '#0D9488',
        hoverBackgroundColor: '#0F766E',
        borderRadius: 8,
        barThickness: 28
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: { stepSize: 1 },
          grid: { color: '#F1F5F9' }
        },
        x: {
          grid: { display: false }
        }
      }
    }
  });
}

function renderStatusChart(statusMap) {
  const ctx = document.getElementById('statusDoughnutChart');
  if (!ctx) return;

  const labels = ['Approved', 'Pending', 'Rejected', 'Cancelled'];
  const values = [
    statusMap.APPROVED || 0,
    statusMap.PENDING || 0,
    statusMap.REJECTED || 0,
    statusMap.CANCELLED || 0
  ];

  if (statusChartInstance) {
    statusChartInstance.destroy();
  }

  statusChartInstance = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: labels,
      datasets: [{
        data: values,
        backgroundColor: [
          '#0D9488', // Approved (Teal)
          '#F59E0B', // Pending (Amber)
          '#EF4444', // Rejected (Red)
          '#94A3B8'  // Cancelled (Slate)
        ],
        borderWidth: 2,
        borderColor: '#FFFFFF'
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom',
          labels: { boxWidth: 12, font: { size: 11 } }
        }
      },
      cutout: '68%'
    }
  });
}

function renderUpcomingWorkshops(list) {
  const tbody = document.getElementById('upcoming-workshops-table');
  if (!tbody) return;

  if (!list || list.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: var(--text-secondary); padding: 24px;">No upcoming workshops scheduled.</td></tr>';
    return;
  }

  tbody.innerHTML = list.map(w => `
    <tr>
      <td>
        <strong style="color: var(--text-main);">${escapeHtml(w.title)}</strong>
        <div style="font-size: 0.78rem; color: var(--brand-primary); font-weight: 600;">${escapeHtml(w.category)}</div>
      </td>
      <td>
        <div>${formatDate(w.workshopDate)}</div>
        <div style="font-size: 0.78rem; color: var(--text-secondary);">${escapeHtml(w.venue)}</div>
      </td>
      <td>
        <div><strong>${w.registeredCount} / ${w.capacity}</strong> filled</div>
        <div style="font-size: 0.78rem; color: var(--text-secondary);">${w.availableSeats} seats left</div>
      </td>
      <td>
        <span class="badge ${w.status === 'UPCOMING' ? 'badge-upcoming' : 'badge-ongoing'}">${w.status}</span>
      </td>
      <td>
        <a href="attendance.html?workshopId=${w.workshopId}" class="btn btn-secondary btn-sm" title="Take Attendance">
          Attendance
        </a>
      </td>
    </tr>
  `).join('');
}

function renderRecentActivities(logs) {
  const container = document.getElementById('recent-activities-list');
  if (!container) return;

  if (!logs || logs.length === 0) {
    container.innerHTML = '<div style="text-align: center; color: var(--text-secondary); padding: 20px;">No recent activities logged.</div>';
    return;
  }

  container.innerHTML = logs.map(log => `
    <div style="display: flex; gap: 12px; align-items: flex-start; font-size: 0.84rem; border-bottom: 1px solid var(--border-light); padding-bottom: 10px;">
      <div style="width: 8px; height: 8px; border-radius: 50%; background: var(--brand-primary); margin-top: 6px; flex-shrink: 0;"></div>
      <div style="flex: 1;">
        <div style="color: var(--text-main); font-weight: 500;">${escapeHtml(log.details || log.action)}</div>
        <div style="font-size: 0.74rem; color: var(--text-secondary); display: flex; gap: 8px; margin-top: 2px;">
          <span>${escapeHtml(log.actorEmail)}</span>
          <span>•</span>
          <span>${log.createdAt ? new Date(log.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'recently'}</span>
        </div>
      </div>
    </div>
  `).join('');
}

