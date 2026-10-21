/**
 * TRIX Workshops - Attendance Tracker Controller
 */

let currentWorkshopId = null;
let currentRoster = [];

document.addEventListener('DOMContentLoaded', async () => {
  const user = await Auth.init('ADMIN');
  if (!user) return;

  const urlParams = new URLSearchParams(window.location.search);
  const paramWsId = urlParams.get('workshopId');

  await loadWorkshops(paramWsId);

  const select = document.getElementById('select-workshop');
  select.addEventListener('change', () => {
    currentWorkshopId = select.value;
    loadAttendanceData();
  });

  const searchInput = document.getElementById('att-search');
  searchInput.addEventListener('input', () => {
    filterRoster(searchInput.value.trim());
  });
});

async function loadWorkshops(preselectId) {
  try {
    const list = await API.get('/api/workshops');
    const select = document.getElementById('select-workshop');

    if (!list || list.length === 0) {
      select.innerHTML = '<option value="">No workshops created yet</option>';
      return;
    }

    select.innerHTML = list.map(w => `
      <option value="${w.workshopId}">${escapeHtml(w.title)} (${formatDate(w.workshopDate)} • ${w.status})</option>
    `).join('');

    if (preselectId && list.some(w => w.workshopId == preselectId)) {
      select.value = preselectId;
    }

    currentWorkshopId = select.value;
    loadAttendanceData();
  } catch (err) {
    showToast('Failed to load workshops list', 'error');
  }
}

async function loadAttendanceData() {
  if (!currentWorkshopId) return;

  const tbody = document.getElementById('attendance-table-body');
  tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 24px;">Loading participant roster...</td></tr>';

  try {
    const [roster, summary] = await Promise.all([
      API.get(`/api/workshops/${currentWorkshopId}/attendance`),
      API.get(`/api/workshops/${currentWorkshopId}/attendance/summary`)
    ]);

    currentRoster = roster;

    // Update analytics cards
    document.getElementById('att-total-expected').textContent = summary.approvedRegistrations || 0;
    document.getElementById('att-present-count').textContent = summary.presentCount || 0;
    document.getElementById('att-absent-count').textContent = summary.absentCount || 0;
    document.getElementById('att-percentage').textContent = (summary.attendancePercentage || 0) + '%';
    document.getElementById('att-progress-fill').style.width = (summary.attendancePercentage || 0) + '%';

    renderTable(roster);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--danger); padding: 24px;">Failed to load attendance: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function filterRoster(query) {
  if (!query) {
    renderTable(currentRoster);
    return;
  }
  const q = query.toLowerCase();
  const filtered = currentRoster.filter(r =>
    (r.participantName && r.participantName.toLowerCase().includes(q)) ||
    (r.participantEmail && r.participantEmail.toLowerCase().includes(q))
  );
  renderTable(filtered);
}

function renderTable(list) {
  const tbody = document.getElementById('attendance-table-body');
  if (!list || list.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="5">
          <div class="empty-state">
            <div class="empty-state-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle></svg>
            </div>
            <h4>No Approved Participants</h4>
            <p>There are no approved participant registrations for this workshop.</p>
          </div>
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = list.map(a => {
    const status = a.attendanceStatus || 'NOT_RECORDED';
    const isPresent = status === 'PRESENT';
    const isAbsent = status === 'ABSENT';
    const isNotRec = status === 'NOT_RECORDED';

    const badgeClass = isPresent ? 'badge-present' : (isAbsent ? 'badge-absent' : 'badge-muted');

    return `
      <tr>
        <td>
          <div style="font-weight: 600; color: var(--text-main);">${escapeHtml(a.participantName)}</div>
          <div style="font-size: 0.74rem; color: var(--text-secondary);">Reg ID: #${a.registrationId}</div>
        </td>
        <td>
          <div style="font-size: 0.86rem; color: var(--text-secondary);">${escapeHtml(a.participantEmail)}</div>
        </td>
        <td>
          <span class="badge ${badgeClass}">${status}</span>
        </td>
        <td>
          <div class="att-btn-group">
            <button onclick="setAttendance(${a.registrationId}, 'PRESENT')" class="att-btn ${isPresent ? 'active-present' : ''}">
              Present
            </button>
            <button onclick="setAttendance(${a.registrationId}, 'ABSENT')" class="att-btn ${isAbsent ? 'active-absent' : ''}">
              Absent
            </button>
            <button onclick="setAttendance(${a.registrationId}, 'NOT_RECORDED')" class="att-btn ${isNotRec ? 'active-not-recorded' : ''}">
              Reset
            </button>
          </div>
        </td>
        <td>
          <div style="font-size: 0.8rem; color: var(--text-secondary);">
            ${a.recordedAt ? new Date(a.recordedAt).toLocaleString([], { dateStyle: 'short', timeStyle: 'short' }) : 'Pending'}
          </div>
          <div style="font-size: 0.72rem; color: var(--text-muted);">
            ${a.recordedBy ? `By: ${escapeHtml(a.recordedBy)}` : ''}
          </div>
        </td>
      </tr>
    `;
  }).join('');
}

async function setAttendance(registrationId, status) {
  try {
    await API.post('/api/attendance/record', {
      registrationId: registrationId,
      status: status
    });
    showToast(`Marked as ${status}`, 'success', 2000);
    await loadAttendanceData();
  } catch (err) {
    showToast(err.message || 'Failed to record attendance', 'error');
  }
}

async function bulkMark(status) {
  if (!currentWorkshopId) return;
  if (!confirm(`Are you sure you want to set all participants to ${status}?`)) return;

  try {
    await API.post(`/api/workshops/${currentWorkshopId}/attendance/bulk`, { status });
    showToast(`Bulk attendance updated to ${status}`, 'success');
    await loadAttendanceData();
  } catch (err) {
    showToast(err.message || 'Failed bulk update', 'error');
  }
}

