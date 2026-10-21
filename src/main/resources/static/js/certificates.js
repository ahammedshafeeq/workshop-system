/**
 * TRIX Workshops - Certificates & Eligibility Controller
 */

let currentWorkshopId = null;
let currentEligibilityList = [];
let allIssuedCerts = [];

document.addEventListener('DOMContentLoaded', async () => {
  const user = await Auth.init('ADMIN');
  if (!user) return;

  await loadWorkshops();

  const select = document.getElementById('select-workshop');
  select.addEventListener('change', () => {
    currentWorkshopId = select.value;
    loadEligibilityData();
  });

  setupIssuedSearch();
});

function switchTab(tabId) {
  document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
  document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

  event.currentTarget.classList.add('active');
  document.getElementById(tabId).classList.add('active');

  if (tabId === 'issued-tab') {
    loadAllIssuedCertificates();
  }
}

async function loadWorkshops() {
  try {
    const list = await API.get('/api/workshops');
    const select = document.getElementById('select-workshop');

    if (!list || list.length === 0) {
      select.innerHTML = '<option value="">No workshops created</option>';
      return;
    }

    select.innerHTML = list.map(w => `
      <option value="${w.workshopId}">${escapeHtml(w.title)} (${formatDate(w.workshopDate)})</option>
    `).join('');

    currentWorkshopId = select.value;
    await loadEligibilityData();
  } catch (err) {
    showToast('Failed to load workshops', 'error');
  }
}

async function loadEligibilityData() {
  if (!currentWorkshopId) return;

  const tbody = document.getElementById('eligibility-table-body');
  tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 24px;">Calculating participant eligibility...</td></tr>';

  try {
    const list = await API.get(`/api/certificates/workshop/${currentWorkshopId}/eligibility`);
    currentEligibilityList = list;

    const eligibleCount = list.filter(item => item.eligibilityStatus === 'ELIGIBLE').length;
    document.getElementById('eligible-count-pill').textContent = `${eligibleCount} Ready for Generation`;

    renderEligibilityTable(list);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--danger); padding: 24px;">Error calculating eligibility: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function renderEligibilityTable(list) {
  const tbody = document.getElementById('eligibility-table-body');
  if (!list || list.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="6">
          <div class="empty-state">
            <div class="empty-state-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="7"></circle></svg>
            </div>
            <h4>No Registrations</h4>
            <p>No participants registered for this workshop yet.</p>
          </div>
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = list.map(item => {
    let statusBadge = '';
    let actionBtn = '';

    const elStatus = item.eligibilityStatus;

    if (elStatus === 'ISSUED') {
      statusBadge = `<span class="badge badge-issued">ISSUED</span>`;
      actionBtn = `
        <a href="/api/certificates/${item.certificateId}/download" class="btn btn-secondary btn-sm" download>
          Download PDF
        </a>
      `;
    } else if (elStatus === 'GENERATED') {
      statusBadge = `<span class="badge badge-generated">GENERATED</span>`;
      actionBtn = `
        <button onclick="issueCertificate(${item.certificateId})" class="btn btn-success btn-sm">Issue</button>
        <a href="/api/certificates/${item.certificateId}/download" class="btn btn-secondary btn-sm" download>PDF</a>
      `;
    } else if (elStatus === 'ELIGIBLE') {
      statusBadge = `<span class="badge badge-eligible">ELIGIBLE</span>`;
      actionBtn = `
        <button onclick="generateSingle(${item.registrationId})" class="btn btn-primary btn-sm">
          Generate Cert
        </button>
      `;
    } else if (elStatus === 'AWAITING_ATTENDANCE') {
      statusBadge = `<span class="badge badge-awaiting">AWAITING ATTENDANCE</span>`;
      actionBtn = `<a href="attendance.html?workshopId=${item.workshopId}" class="btn btn-tint btn-sm">Mark Attendance</a>`;
    } else {
      statusBadge = `<span class="badge badge-not-eligible">NOT ELIGIBLE</span>`;
      actionBtn = `<span style="font-size: 0.78rem; color: var(--text-muted);">Ineligible</span>`;
    }

    return `
      <tr>
        <td>
          <div style="font-weight: 600; color: var(--text-main);">${escapeHtml(item.participantName)}</div>
          <div style="font-size: 0.8rem; color: var(--text-secondary);">${escapeHtml(item.participantEmail)}</div>
          <div style="font-size: 0.74rem; color: var(--text-muted);">${escapeHtml(item.department || '')}</div>
        </td>
        <td>
          <span class="badge ${item.registrationStatus === 'APPROVED' ? 'badge-approved' : 'badge-rejected'}">
            ${item.registrationStatus}
          </span>
        </td>
        <td>
          <span class="badge ${item.attendanceStatus === 'PRESENT' ? 'badge-present' : (item.attendanceStatus === 'ABSENT' ? 'badge-absent' : 'badge-muted')}">
            ${item.attendanceStatus}
          </span>
        </td>
        <td>${statusBadge}</td>
        <td>
          <div style="font-size: 0.82rem; color: var(--text-secondary); max-width: 260px;">
            ${escapeHtml(item.reason)}
          </div>
          ${item.certificateNumber ? `<div style="font-size: 0.75rem; font-family: monospace; color: var(--brand-primary); margin-top: 3px;">${escapeHtml(item.certificateNumber)}</div>` : ''}
        </td>
        <td>${actionBtn}</td>
      </tr>
    `;
  }).join('');
}

async function generateSingle(registrationId) {
  try {
    const res = await API.post(`/api/registrations/${registrationId}/certificate`, {});
    showToast(`Certificate ${res.certificateNumber} generated successfully!`, 'success');
    await loadEligibilityData();
  } catch (err) {
    showToast(err.message || 'Generation failed.', 'error');
  }
}

async function bulkGenerate() {
  if (!currentWorkshopId) return;
  const btn = document.getElementById('btn-bulk-generate');
  btn.disabled = true;
  btn.textContent = 'Generating PDFs...';

  try {
    const result = await API.post(`/api/workshops/${currentWorkshopId}/certificates/bulk-generate`, {});
    showToast(`Bulk generation complete! Created ${result.generatedCount} certificates.`, 'success');
    await loadEligibilityData();
  } catch (err) {
    showToast(err.message || 'Bulk generation failed.', 'error');
  } finally {
    btn.disabled = false;
    btn.innerHTML = `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"></path></svg> Bulk Generate for All Eligible`;
  }
}

async function issueCertificate(certId) {
  try {
    await API.post(`/api/certificates/${certId}/issue`, {});
    showToast('Certificate issued to participant.', 'success');
    await loadEligibilityData();
  } catch (err) {
    showToast(err.message || 'Failed to issue certificate.', 'error');
  }
}

async function issueAllGenerated() {
  if (!currentWorkshopId) return;
  if (!confirm('Issue all generated certificates for this workshop? Participants will be able to download them immediately.')) return;

  try {
    await API.post(`/api/workshops/${currentWorkshopId}/certificates/issue-all`, {});
    showToast('All generated certificates have been released.', 'success');
    await loadEligibilityData();
  } catch (err) {
    showToast(err.message || 'Failed to issue certificates.', 'error');
  }
}

// TAB 2: ISSUED CERTIFICATES
async function loadAllIssuedCertificates() {
  const tbody = document.getElementById('issued-certs-table-body');
  tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 24px;">Loading issued certificates archive...</td></tr>';

  try {
    const list = await API.get('/api/certificates');
    allIssuedCerts = list;
    renderIssuedTable(list);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--danger); padding: 24px;">Failed to load certificates: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function setupIssuedSearch() {
  const searchInput = document.getElementById('cert-search');
  const statusFilter = document.getElementById('cert-status-filter');

  const filterAction = () => {
    const q = searchInput.value.toLowerCase().trim();
    const st = statusFilter.value;

    const filtered = allIssuedCerts.filter(c => {
      const matchQ = !q ||
        (c.participantName && c.participantName.toLowerCase().includes(q)) ||
        (c.certificateNumber && c.certificateNumber.toLowerCase().includes(q)) ||
        (c.verificationCode && c.verificationCode.toLowerCase().includes(q));

      const matchSt = st === 'ALL' || c.certificateStatus === st;
      return matchQ && matchSt;
    });

    renderIssuedTable(filtered);
  };

  searchInput.addEventListener('input', filterAction);
  statusFilter.addEventListener('change', filterAction);
}

function renderIssuedTable(list) {
  const tbody = document.getElementById('issued-certs-table-body');
  if (!list || list.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="6">
          <div class="empty-state">
            <h4>No Certificates Found</h4>
            <p>No generated certificates match the search criteria.</p>
          </div>
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = list.map(c => `
    <tr>
      <td>
        <strong style="font-family: monospace; color: var(--brand-primary);">${escapeHtml(c.certificateNumber)}</strong>
        <div style="font-size: 0.74rem; color: var(--text-muted);">Verification: ${escapeHtml(c.verificationCode)}</div>
      </td>
      <td>
        <div style="font-weight: 600; color: var(--text-main);">${escapeHtml(c.participantName)}</div>
        <div style="font-size: 0.8rem; color: var(--text-secondary);">${escapeHtml(c.participantEmail)}</div>
      </td>
      <td>
        <div style="color: var(--text-main); font-weight: 500;">${escapeHtml(c.workshopTitle)}</div>
        <div style="font-size: 0.76rem; color: var(--text-secondary);">${formatDate(c.workshopDate)}</div>
      </td>
      <td>${formatDate(c.issueDate)}</td>
      <td>
        <span class="badge ${c.certificateStatus === 'ISSUED' ? 'badge-issued' : 'badge-generated'}">
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

