/**
 * TRIX Workshops - Participant Dashboard Controller
 */

let currentUserProfile = null;

document.addEventListener('DOMContentLoaded', async () => {
  const user = await Auth.init('PARTICIPANT');
  if (!user) return;

  currentUserProfile = user;

  const dateEl = document.getElementById('participant-date');
  if (dateEl) {
    dateEl.textContent = new Date().toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  // Pre-fill profile modal
  document.getElementById('prof-name').value = user.fullName || '';
  document.getElementById('prof-email').value = user.email || '';
  document.getElementById('prof-phone').value = user.phone || '';
  document.getElementById('prof-dept').value = user.department || '';
  document.getElementById('prof-inst').value = user.institution || '';

  setupProfileForm();
  await loadParticipantDashboard();
});

async function loadParticipantDashboard() {
  try {
    const [stats, myRegistrations, myCertificates] = await Promise.all([
      API.get('/api/dashboard/participant'),
      API.get('/api/registrations/my'),
      API.get('/api/certificates/my')
    ]);

    // Update stat cards
    document.getElementById('part-stat-registered').textContent = stats.participantRegisteredCount || 0;
    document.getElementById('part-stat-upcoming').textContent = stats.participantUpcomingCount || 0;
    document.getElementById('part-stat-completed').textContent = stats.participantCompletedCount || 0;
    document.getElementById('part-stat-certificates').textContent = stats.participantCertificatesCount || 0;

    renderRegistrationsTable(myRegistrations || []);
    renderCertificatesTable(myCertificates || []);
  } catch (err) {
    showToast('Failed to load dashboard data: ' + err.message, 'error');
  }
}

function renderRegistrationsTable(list) {
  const tbody = document.getElementById('my-workshops-table');
  if (!list || list.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="6">
          <div class="empty-state">
            <div class="empty-state-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path></svg>
            </div>
            <h4>You have not registered for any workshops yet</h4>
            <p>Browse our catalog of technical workshops and reserve your seat today.</p>
            <a href="workshops.html" class="btn btn-primary btn-sm">Explore Workshops</a>
          </div>
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = list.map(r => {
    let certColumn = '<span style="font-size: 0.78rem; color: var(--text-muted);">Not Available</span>';

    if (r.certificateId) {
      certColumn = `
        <a href="/api/certificates/${r.certificateId}/download" class="btn btn-primary btn-sm" download>
          Download PDF
        </a>
      `;
    } else if (r.attendanceStatus === 'PRESENT') {
      certColumn = '<span class="badge badge-eligible">Eligible (Awaiting Release)</span>';
    } else if (r.attendanceStatus === 'ABSENT') {
      certColumn = '<span class="badge badge-not-eligible">Ineligible (Absent)</span>';
    }

    let actionBtn = '';
    if (r.registrationStatus === 'APPROVED' || r.registrationStatus === 'PENDING') {
      actionBtn = `
        <button onclick="cancelRegistration(${r.registrationId}, '${escapeHtml(r.workshopTitle)}')" class="btn btn-secondary btn-sm" style="color: var(--danger); font-size: 0.78rem;">
          Cancel Seat
        </button>
      `;
    }

    return `
      <tr>
        <td>
          <div style="font-weight: 600; color: var(--text-main);">${escapeHtml(r.workshopTitle)}</div>
          <div style="font-size: 0.78rem; color: var(--brand-primary); font-weight: 500;">${escapeHtml(r.workshopCategory)}</div>
        </td>
        <td>
          <div>${formatDate(r.workshopDate)}</div>
          <div style="font-size: 0.78rem; color: var(--text-secondary);">${escapeHtml(r.workshopVenue)}</div>
        </td>
        <td>
          <span class="badge ${r.registrationStatus === 'APPROVED' ? 'badge-approved' : (r.registrationStatus === 'PENDING' ? 'badge-pending' : 'badge-rejected')}">
            ${r.registrationStatus}
          </span>
        </td>
        <td>
          <span class="badge ${r.attendanceStatus === 'PRESENT' ? 'badge-present' : (r.attendanceStatus === 'ABSENT' ? 'badge-absent' : 'badge-muted')}">
            ${r.attendanceStatus}
          </span>
        </td>
        <td>${certColumn}</td>
        <td>${actionBtn}</td>
      </tr>
    `;
  }).join('');
}

function renderCertificatesTable(list) {
  const tbody = document.getElementById('my-certificates-table');
  if (!list || list.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="6">
          <div class="empty-state">
            <h4>No Issued Certificates Yet</h4>
            <p>Attend registered workshops to become eligible for verified completion certificates.</p>
          </div>
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = list.map(c => `
    <tr>
      <td>
        <strong style="color: var(--text-main);">${escapeHtml(c.workshopTitle)}</strong>
        <div style="font-size: 0.76rem; color: var(--text-secondary);">${formatDate(c.workshopDate)} • ${escapeHtml(c.venue)}</div>
      </td>
      <td>
        <span style="font-family: monospace; font-weight: 600; color: var(--brand-primary);">${escapeHtml(c.certificateNumber)}</span>
      </td>
      <td>
        <span style="font-family: monospace; font-size: 0.8rem; background: #F1F5F9; padding: 2px 6px; border-radius: 4px;">${escapeHtml(c.verificationCode)}</span>
      </td>
      <td>${formatDate(c.issueDate)}</td>
      <td>
        <span class="badge badge-issued">
          ${c.certificateStatus}
        </span>
      </td>
      <td>
        <a href="/api/certificates/${c.certificateId}/download" class="btn btn-primary btn-sm" download>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
          Download PDF
        </a>
      </td>
    </tr>
  `).join('');
}

async function cancelRegistration(registrationId, title) {
  if (!confirm(`Are you sure you want to cancel your seat for "${title}"?`)) {
    return;
  }

  try {
    await API.post(`/api/registrations/${registrationId}/cancel`, {});
    showToast('Registration cancelled.', 'info');
    await loadParticipantDashboard();
  } catch (err) {
    showToast(err.message || 'Could not cancel registration.', 'error');
  }
}

function setupProfileForm() {
  const form = document.getElementById('profile-form');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('save-profile-btn');
    btn.disabled = true;
    btn.textContent = 'Saving...';

    const payload = {
      fullName: document.getElementById('prof-name').value.trim(),
      phone: document.getElementById('prof-phone').value.trim(),
      department: document.getElementById('prof-dept').value.trim(),
      institution: document.getElementById('prof-inst').value.trim()
    };

    try {
      const updated = await API.put('/api/auth/profile', payload);
      showToast('Profile updated successfully!', 'success');
      closeModal('profile-modal');
      Auth.renderUserUI(updated);
    } catch (err) {
      showToast(err.message || 'Failed to update profile.', 'error');
    } finally {
      btn.disabled = false;
      btn.textContent = 'Save Changes';
    }
  });
}

