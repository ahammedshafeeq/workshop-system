/**
 * TRIX Workshops - Reporting & Data Exports Controller
 */

let activeReportType = 'workshops';

document.addEventListener('DOMContentLoaded', async () => {
  const user = await Auth.init('ADMIN');
  if (!user) return;

  await loadWorkshopsFilter();
  await loadReport();
});

async function loadWorkshopsFilter() {
  try {
    const list = await API.get('/api/workshops');
    const select = document.getElementById('rep-workshop');
    select.innerHTML = '<option value="">All Workshops</option>' +
      list.map(w => `<option value="${w.workshopId}">${escapeHtml(w.title)}</option>`).join('');
  } catch (err) {
    console.error('Failed to load workshops filter', err);
  }
}

function selectReport(type) {
  activeReportType = type;
  document.querySelectorAll('.report-tab-pill').forEach(btn => btn.classList.remove('active'));
  event.currentTarget.classList.add('active');

  // Toggle filter visibility based on report
  const catWrap = document.getElementById('filter-category-wrap');
  const wsWrap = document.getElementById('filter-workshop-wrap');

  if (type === 'capacity') {
    catWrap.style.display = 'none';
    wsWrap.style.display = 'none';
  } else if (type === 'workshops') {
    catWrap.style.display = 'block';
    wsWrap.style.display = 'none';
  } else {
    catWrap.style.display = 'none';
    wsWrap.style.display = 'block';
  }

  loadReport();
}

function applyReportFilters() {
  loadReport();
}

async function loadReport() {
  const wsId = document.getElementById('rep-workshop').value;
  const category = document.getElementById('rep-category').value;
  const startDate = document.getElementById('rep-start-date').value;
  const endDate = document.getElementById('rep-end-date').value;

  const params = new URLSearchParams();
  if (wsId) params.append('workshopId', wsId);
  if (category && category !== 'ALL') params.append('category', category);
  if (startDate) params.append('startDate', startDate);
  if (endDate) params.append('endDate', endDate);

  const tbody = document.getElementById('report-table-body');
  tbody.innerHTML = '<tr><td colspan="10" style="text-align: center; padding: 24px;">Generating report dataset...</td></tr>';

  try {
    const report = await API.get(`/api/reports/${activeReportType}?${params.toString()}`);

    document.getElementById('rep-display-title').textContent = report.reportName || 'Report';
    document.getElementById('rep-record-count').textContent = `${report.totalRecords || 0} records`;
    document.getElementById('rep-generated-at').textContent = report.generatedAt || new Date().toLocaleString();

    renderReportTable(report.data || []);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="10" style="text-align: center; color: var(--danger); padding: 24px;">Failed to load report: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function renderReportTable(data) {
  const thead = document.getElementById('report-table-head');
  const tbody = document.getElementById('report-table-body');

  if (!data || data.length === 0) {
    thead.innerHTML = '';
    tbody.innerHTML = `
      <tr>
        <td style="text-align: center; padding: 36px;">
          <div class="empty-state">
            <h4>No Records Found</h4>
            <p>No records matched the filter criteria for this report.</p>
          </div>
        </td>
      </tr>
    `;
    return;
  }

  // Derive column headers from first record keys
  const keys = Object.keys(data[0]);

  thead.innerHTML = `
    <tr>
      ${keys.map(k => `<th>${formatHeader(k)}</th>`).join('')}
    </tr>
  `;

  tbody.innerHTML = data.map(row => `
    <tr>
      ${keys.map(k => `<td>${formatCellValue(row[k])}</td>`).join('')}
    </tr>
  `).join('');
}

function formatHeader(key) {
  return key.replace(/_/g, ' ').toUpperCase();
}

function formatCellValue(val) {
  if (val === null || val === undefined) return '<span style="color: var(--text-muted);">-</span>';
  return escapeHtml(String(val));
}

function downloadActiveCsv() {
  const wsId = document.getElementById('rep-workshop').value;
  const category = document.getElementById('rep-category').value;
  const startDate = document.getElementById('rep-start-date').value;
  const endDate = document.getElementById('rep-end-date').value;

  const params = new URLSearchParams();
  params.append('type', activeReportType);
  if (wsId) params.append('workshopId', wsId);
  if (category && category !== 'ALL') params.append('category', category);
  if (startDate) params.append('startDate', startDate);
  if (endDate) params.append('endDate', endDate);

  window.location.href = `/api/reports/export/csv?${params.toString()}`;
  showToast('Initiated sanitized CSV download.', 'info');
}

