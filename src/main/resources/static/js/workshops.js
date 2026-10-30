/**
 * TRIX Workshops - Workshops Directory & Management
 */

let allWorkshops = [];
let currentUser = null;
let activeModalWorkshopId = null;

document.addEventListener('DOMContentLoaded', async () => {
  currentUser = await Auth.init(); // No forced role redirect - accessible by both or visitors

  // Check query param for direct workshop modal open
  const urlParams = new URLSearchParams(window.location.search);
  const targetId = urlParams.get('id');

  setupFilters();
  setupWorkshopForm();
  await loadWorkshops();

  if (targetId) {
    openDetailsModal(parseInt(targetId, 10));
  }
});

function setupFilters() {
  const searchInput = document.getElementById('filter-search');
  const catSelect = document.getElementById('filter-category');
  const statusSelect = document.getElementById('filter-status');
  const resetBtn = document.getElementById('reset-filters-btn');

  let debounceTimer;
  searchInput.addEventListener('input', () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(loadWorkshops, 250);
  });

  catSelect.addEventListener('change', loadWorkshops);
  statusSelect.addEventListener('change', loadWorkshops);

  resetBtn.addEventListener('click', () => {
    searchInput.value = '';
    catSelect.value = 'ALL';
    statusSelect.value = 'ALL';
    loadWorkshops();
  });
}

async function loadWorkshops() {
  const search = document.getElementById('filter-search').value.trim();
  const category = document.getElementById('filter-category').value;
  const status = document.getElementById('filter-status').value;

  const params = new URLSearchParams();
  if (search) params.append('search', search);
  if (category && category !== 'ALL') params.append('category', category);
  if (status && status !== 'ALL') params.append('status', status);

  const tbody = document.getElementById('workshops-table-body');
  tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 24px;">Loading workshops...</td></tr>';

  try {
    const list = await API.get('/api/workshops?' + params.toString());
    allWorkshops = list;
    renderTable(list);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--danger); padding: 24px;">Failed to load workshops: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function renderTable(list) {
  const tbody = document.getElementById('workshops-table-body');
  if (!list || list.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="6">
          <div class="empty-state">
            <div class="empty-state-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path></svg>
            </div>
            <h4>No Workshops Found</h4>
            <p>No workshops match the selected search or filter criteria.</p>
          </div>
        </td>
      </tr>
    `;
    return;
  }

  const isAdmin = currentUser && currentUser.role === 'ADMIN';

  tbody.innerHTML = list.map(w => {
    let actionBtn = '';

    if (isAdmin) {
      actionBtn = `
        <div style="display: flex; gap: 6px;">
          <button onclick="openEditModal(${w.workshopId})" class="btn btn-secondary btn-sm" title="Edit">
            Edit
          </button>
          <a href="attendance.html?workshopId=${w.workshopId}" class="btn btn-tint btn-sm" title="Attendance">
            Attendance
          </a>
          <button onclick="deleteWorkshop(${w.workshopId}, '${escapeHtml(w.title)}')" class="btn btn-danger btn-sm" title="Delete">
            Del
          </button>
        </div>
      `;
    } else {
      if (w.userRegistered) {
        actionBtn = `
          <button onclick="openDetailsModal(${w.workshopId})" class="btn btn-tint btn-sm">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>
            Registered
          </button>
        `;
      } else if (w.isRegistrationOpen) {
        actionBtn = `
          <button onclick="openDetailsModal(${w.workshopId})" class="btn btn-primary btn-sm">
            Register Now
          </button>
        `;
      } else {
        actionBtn = `
          <button onclick="openDetailsModal(${w.workshopId})" class="btn btn-secondary btn-sm">
            View Details
          </button>
        `;
      }
    }

    return `
      <tr>
        <td>
          <a href="javascript:void(0)" onclick="openDetailsModal(${w.workshopId})" style="font-weight: 600; color: var(--text-main); text-decoration: none;">
            ${escapeHtml(w.title)}
          </a>
          <div style="font-size: 0.8rem; color: var(--text-secondary); margin-top: 2px;">
            Resource Person: ${escapeHtml(w.organizerName)}
          </div>
        </td>
        <td>
          <span class="badge badge-ongoing">${escapeHtml(w.category)}</span>
        </td>
        <td>
          <div>${formatDate(w.workshopDate)}</div>
          <div style="font-size: 0.78rem; color: var(--text-secondary);">
            ${formatTime(w.startTime)} - ${formatTime(w.endTime)} • ${escapeHtml(w.venue)}
          </div>
        </td>
        <td>
          <div><strong>${w.registeredCount} / ${w.capacity}</strong> filled</div>
          <div class="progress-bar-wrap" style="width: 100px;">
            <div class="progress-bar-fill" style="width: ${Math.min(100, Math.round((w.registeredCount / w.capacity) * 100))}%;"></div>
          </div>
          <div style="font-size: 0.74rem; color: ${w.availableSeats > 0 ? 'var(--success)' : 'var(--danger)'}; margin-top: 3px;">
            ${w.availableSeats > 0 ? `${w.availableSeats} seats left` : 'Fully booked'}
          </div>
        </td>
        <td>
          <span class="badge ${w.status === 'UPCOMING' ? 'badge-upcoming' : (w.status === 'ONGOING' ? 'badge-ongoing' : (w.status === 'COMPLETED' ? 'badge-completed' : 'badge-rejected'))}">
            ${w.status}
          </span>
        </td>
        <td>${actionBtn}</td>
      </tr>
    `;
  }).join('');
}

function openDetailsModal(workshopId) {
  const w = allWorkshops.find(item => item.workshopId === workshopId);
  if (!w) return;

  activeModalWorkshopId = workshopId;

  document.getElementById('modal-ws-title').textContent = w.title;
  document.getElementById('modal-ws-desc').textContent = w.description || 'No detailed syllabus specified.';
  document.getElementById('modal-ws-category').textContent = w.category;
  document.getElementById('modal-ws-organizer').textContent = w.organizerName;
  document.getElementById('modal-ws-venue').textContent = w.venue;
  document.getElementById('modal-ws-date').textContent = formatDate(w.workshopDate);
  document.getElementById('modal-ws-time').textContent = `${formatTime(w.startTime)} to ${formatTime(w.endTime)}`;
  document.getElementById('modal-ws-capacity').textContent = `${w.registeredCount} / ${w.capacity} (${w.availableSeats} remaining)`;
  document.getElementById('modal-ws-deadline').textContent = formatDate(w.registrationDeadline);

  const statusEl = document.getElementById('modal-ws-status');
  statusEl.textContent = w.status;
  statusEl.className = `badge ${w.status === 'UPCOMING' ? 'badge-upcoming' : (w.status === 'ONGOING' ? 'badge-ongoing' : 'badge-completed')}`;

  const notesGroup = document.getElementById('enroll-notes-group');
  const confirmBtn = document.getElementById('confirm-register-btn');
  const warnEl = document.getElementById('enroll-warning');

  warnEl.style.display = 'none';

  if (!currentUser) {
    confirmBtn.style.display = 'inline-flex';
    confirmBtn.textContent = 'Sign In to Register';
    confirmBtn.onclick = () => {
      window.location.href = 'login.html?redirect=' + encodeURIComponent('workshops.html?id=' + activeModalWorkshopId);
    };
    notesGroup.style.display = 'none';
  } else if (currentUser.role === 'ADMIN') {
    confirmBtn.style.display = 'none';
    notesGroup.style.display = 'none';
  } else {
    // Participant
    if (w.userRegistered) {
      confirmBtn.style.display = 'none';
      notesGroup.style.display = 'none';
      warnEl.style.display = 'block';
      warnEl.style.background = 'var(--success-bg)';
      warnEl.style.color = 'var(--success)';
      warnEl.innerHTML = `<strong>You are registered!</strong> Status: ${w.userRegistrationStatus || 'APPROVED'}. Visit your dashboard to track attendance and certificate.`;
    } else if (!w.isRegistrationOpen) {
      confirmBtn.style.display = 'none';
      notesGroup.style.display = 'none';
      warnEl.style.display = 'block';
      warnEl.style.background = 'var(--warning-bg)';
      warnEl.style.color = 'var(--warning)';
      warnEl.textContent = 'Registration is closed for this session (Capacity reached or deadline passed).';
    } else {
      confirmBtn.style.display = 'inline-flex';
      confirmBtn.textContent = 'Confirm Registration';
      confirmBtn.onclick = submitRegistration;
      notesGroup.style.display = 'block';
    }
  }

  openModal('workshop-details-modal');
}

async function submitRegistration() {
  if (!activeModalWorkshopId) return;

  const btn = document.getElementById('confirm-register-btn');
  btn.disabled = true;
  btn.textContent = 'Enrolling...';

  const notes = document.getElementById('reg-notes-input').value.trim();

  try {
    await API.post(`/api/workshops/${activeModalWorkshopId}/registrations`, { notes });
    showToast('Successfully registered for the workshop!', 'success');
    closeModal('workshop-details-modal');
    await loadWorkshops();
  } catch (err) {
    showToast(err.message || 'Registration failed.', 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Confirm Registration';
  }
}

function setupWorkshopForm() {
  const form = document.getElementById('workshop-form');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('modal-submit-btn');
    btn.disabled = true;
    btn.textContent = 'Saving...';

    const id = document.getElementById('edit-workshop-id').value;
    const payload = {
      title: document.getElementById('ws-title-input').value.trim(),
      description: document.getElementById('ws-desc-input').value.trim(),
      category: document.getElementById('ws-cat-input').value,
      organizerName: document.getElementById('ws-org-input').value.trim(),
      workshopDate: document.getElementById('ws-date-input').value,
      startTime: document.getElementById('ws-start-input').value + (document.getElementById('ws-start-input').value.length === 5 ? ':00' : ''),
      endTime: document.getElementById('ws-end-input').value + (document.getElementById('ws-end-input').value.length === 5 ? ':00' : ''),
      venue: document.getElementById('ws-venue-input').value.trim(),
      capacity: parseInt(document.getElementById('ws-cap-input').value, 10),
      registrationDeadline: document.getElementById('ws-dead-input').value,
      status: document.getElementById('ws-status-input').value
    };

    try {
      if (id) {
        await API.put(`/api/workshops/${id}`, payload);
        showToast('Workshop updated successfully!', 'success');
      } else {
        await API.post('/api/workshops', payload);
        showToast('Workshop published successfully!', 'success');
      }
      closeModal('create-workshop-modal');
      await loadWorkshops();
    } catch (err) {
      showToast(err.message || 'Failed to save workshop', 'error');
    } finally {
      btn.disabled = false;
      btn.textContent = 'Save Workshop';
    }
  });
}

function openEditModal(workshopId) {
  const w = allWorkshops.find(item => item.workshopId === workshopId);
  if (!w) return;

  document.getElementById('create-modal-title').textContent = 'Edit Workshop';
  document.getElementById('edit-workshop-id').value = w.workshopId;
  document.getElementById('ws-title-input').value = w.title;
  document.getElementById('ws-desc-input').value = w.description || '';
  document.getElementById('ws-cat-input').value = w.category;
  document.getElementById('ws-org-input').value = w.organizerName;
  document.getElementById('ws-date-input').value = w.workshopDate;
  document.getElementById('ws-start-input').value = w.startTime ? w.startTime.substring(0, 5) : '09:30';
  document.getElementById('ws-end-input').value = w.endTime ? w.endTime.substring(0, 5) : '16:30';
  document.getElementById('ws-venue-input').value = w.venue;
  document.getElementById('ws-cap-input').value = w.capacity;
  document.getElementById('ws-dead-input').value = w.registrationDeadline;
  document.getElementById('ws-status-input').value = w.status;

  document.getElementById('status-group').style.display = 'block';

  openModal('create-workshop-modal');
}

async function deleteWorkshop(workshopId, title) {
  if (!confirm(`Are you sure you want to delete workshop "${title}"? This will remove related registrations.`)) {
    return;
  }

  try {
    await API.delete(`/api/workshops/${workshopId}`);
    showToast('Workshop deleted successfully.', 'info');
    await loadWorkshops();
  } catch (err) {
    showToast(err.message || 'Failed to delete workshop.', 'error');
  }
}

