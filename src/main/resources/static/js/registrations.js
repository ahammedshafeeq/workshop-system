/**
 * TRIX Workshops - Registrations Management
 */

document.addEventListener('DOMContentLoaded', async () => {
  const user = await Auth.init('ADMIN');
  if (!user) return;

  await loadWorkshopsDropdown();
  setupFilters();
  await loadRegistrations();
});

async function loadWorkshopsDropdown() {
  try {
    const list = await API.get('/api/workshops');
    const select = document.getElementById('filter-workshop');
    select.innerHTML = '<option value="">All Workshops</option>' +
      list.map(w => `<option value="${w.workshopId}">${escapeHtml(w.title)} (${formatDate(w.workshopDate)})</option>`).join('');
  } catch (err) {
    console.error('Failed to load workshops dropdown', err);
  }
}

function setupFilters() {
  const searchInput = document.getElementById('filter-search');
  const wsSelect = document.getElementById('filter-workshop');
  const statusSelect = document.getElementById('filter-status');
  const resetBtn = document.getElementById('reset-filters-btn');

  let debounceTimer;
  searchInput.addEventListener('input', () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(loadRegistrations, 250);
  });

  wsSelect.addEventListener('change', loadRegistrations);
  statusSelect.addEventListener('change', loadRegistrations);

  resetBtn.addEventListener('click', () => {
    searchInput.value = '';
    wsSelect.value = '';
    statusSelect.value = 'ALL';
    loadRegistrations();
  });
}

async function loadRegistrations() {
  const search = document.getElementById('filter-search').value.trim();
  const workshopId = document.getElementById('filter-workshop').value;
  const status = document.getElementById('filter-status').value;

  const params = new URLSearchParams();
  if (search) params.append('search', search);
  if (workshopId) params.append('workshopId', workshopId);
  if (status && status !== 'ALL') params.append('status', status);

  const tbody = document.getElementById('registrations-table-body');
  tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 24px;">Loading registrations...</td></tr>';

  try {
    const list = await API.get('/api/registrations?' + params.toString());
    document.getElementById('total-reg-count').textContent = `${list.length} Registrations`;
    renderTable(list);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--danger); padding: 24px;">Failed to load registrations: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function renderTable(list) {
  const tbody = document.getElementById('registrations-table-body');
  if (!list || list.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="6">
          <div class="empty-state">
            <div class="empty-state-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle></svg>
            </div>
            <h4>No Registrations Found</h4>
            <p>No participant enrollments match the current filter selection.</p>
          </div>
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = list.map(r => {
    let actionButtons = '';
    if (r.registrationStatus === 'PENDING') {
      actionButtons = `
        <button onclick="updateStatus(${r.registrationId}, 'APPROVED')" class="btn btn-success btn-sm">Approve</button>
        <button onclick="updateStatus(${r.registrationId}, 'REJECTED')" class="btn btn-danger btn-sm">Reject</button>
      `;
    } else if (r.registrationStatus === 'APPROVED') {
      actionButtons = `
        <button onclick="updateStatus(${r.registrationId}, 'REJECTED')" class="btn btn-secondary btn-sm" title="Reject Seat">
          Reject
        </button>
      `;
    } else if (r.registrationStatus === 'REJECTED') {
      actionButtons = `
        <button onclick="updateStatus(${r.registrationId}, 'APPROVED')" class="btn btn-tint btn-sm" title="Re-approve Seat">
          Approve
        </button>
      `;
    } else {
      actionButtons = `<span style="font-size: 0.8rem; color: var(--text-muted);">Cancelled</span>`;
    }

    const attBadgeClass = r.attendanceStatus === 'PRESENT' ? 'badge-present' : (r.attendanceStatus === 'ABSENT' ? 'badge-absent' : 'badge-muted');

    return `
      <tr>
        <td>
          <div style="font-weight: 600; color: var(--text-main);">${escapeHtml(r.participantName)}</div>
          <div style="font-size: 0.8rem; color: var(--text-secondary);">${escapeHtml(r.participantEmail)}</div>
          <div style="font-size: 0.74rem; color: var(--text-muted); margin-top: 2px;">
            ${escapeHtml(r.participantDepartment || '')} ${r.participantInstitution ? '• ' + escapeHtml(r.participantInstitution) : ''}
          </div>
        </td>
        <td>
          <strong style="color: var(--text-main);">${escapeHtml(r.workshopTitle)}</strong>
          <div style="font-size: 0.78rem; color: var(--brand-primary);">${escapeHtml(r.workshopCategory)}</div>
          <div style="font-size: 0.74rem; color: var(--text-secondary);">${formatDate(r.workshopDate)} • ${escapeHtml(r.workshopVenue)}</div>
        </td>
        <td>
          <div style="font-size: 0.84rem;">${formatDate(r.registrationDate)}</div>
          <div style="font-size: 0.74rem; color: var(--text-muted);">${r.notes ? escapeHtml(r.notes) : 'Standard'}</div>
        </td>
        <td>
          <span class="badge ${r.registrationStatus === 'APPROVED' ? 'badge-approved' : (r.registrationStatus === 'PENDING' ? 'badge-pending' : (r.registrationStatus === 'REJECTED' ? 'badge-rejected' : 'badge-cancelled'))}">
            ${r.registrationStatus}
          </span>
        </td>
        <td>
          <span class="badge ${attBadgeClass}">
            ${r.attendanceStatus}
          </span>
          ${r.certificateStatus ? `<div style="margin-top: 4px;"><span class="badge badge-issued">Cert: ${r.certificateStatus}</span></div>` : ''}
        </td>
        <td>
          <div style="display: flex; gap: 6px;">
            ${actionButtons}
          </div>
        </td>
      </tr>
    `;
  }).join('');
}

async function updateStatus(registrationId, newStatus) {
  try {
    await API.patch(`/api/registrations/${registrationId}/status`, { status: newStatus });
    showToast(`Registration marked as ${newStatus}`, 'success');
    await loadRegistrations();
  } catch (err) {
    showToast(err.message || 'Status update failed.', 'error');
  }
}

