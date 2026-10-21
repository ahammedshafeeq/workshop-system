/**
 * TRIX Workshops - Public Certificate Verification
 */

document.addEventListener('DOMContentLoaded', () => {
  const urlParams = new URLSearchParams(window.location.search);
  const paramCode = urlParams.get('code');

  if (paramCode) {
    document.getElementById('verify-input').value = paramCode;
    verifyCode(paramCode);
  }

  const form = document.getElementById('verify-form');
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const code = document.getElementById('verify-input').value.trim();
    if (code) {
      verifyCode(code);
    }
  });
});

async function verifyCode(code) {
  const btn = document.getElementById('verify-btn');
  const card = document.getElementById('verified-card');

  btn.disabled = true;
  btn.textContent = 'Verifying...';

  try {
    const cert = await API.get(`/api/certificates/verify/${encodeURIComponent(code)}`);

    document.getElementById('res-name').textContent = cert.participantName;
    document.getElementById('res-workshop').textContent = cert.workshopTitle;
    document.getElementById('res-date').textContent = formatDate(cert.workshopDate);
    document.getElementById('res-venue').textContent = cert.venue;
    document.getElementById('res-cert-id').textContent = cert.certificateNumber;
    document.getElementById('res-code').textContent = cert.verificationCode;
    document.getElementById('res-issue-date').textContent = formatDate(cert.issueDate);

    card.style.display = 'block';
    card.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    showToast('Certificate successfully verified!', 'success');
  } catch (err) {
    card.style.display = 'none';
    showToast(err.message || 'No valid certificate found matching the code provided.', 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Verify Authenticity';
  }
}

