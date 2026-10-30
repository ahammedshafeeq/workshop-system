/**
 * TRIX Workshops - Authentication & Session Management
 */

const Auth = {
  currentUser: null,

  async init(requiredRole = null) {
    try {
      const user = await API.get('/api/auth/me');
      this.currentUser = user;
      this.renderUserUI(user);

      if (requiredRole && user.role !== requiredRole) {
        showToast(`Access restricted to ${requiredRole.toLowerCase()}s.`, 'error');
        setTimeout(() => {
          if (user.role === 'ADMIN') {
            window.location.href = 'admin-dashboard.html';
          } else {
            window.location.href = 'participant-dashboard.html';
          }
        }, 800);
        return null;
      }

      return user;
    } catch (err) {
      this.currentUser = null;
      if (requiredRole) {
        // Protected page accessed without session
        window.location.href = 'login.html?redirect=' + encodeURIComponent(window.location.pathname);
      }
      return null;
    }
  },

  renderUserUI(user) {
    const nameEls = document.querySelectorAll('.user-display-name');
    nameEls.forEach(el => el.textContent = user.fullName);

    const emailEls = document.querySelectorAll('.user-display-email');
    emailEls.forEach(el => el.textContent = user.email);

    const roleEls = document.querySelectorAll('.user-display-role');
    roleEls.forEach(el => el.textContent = user.role === 'ADMIN' ? 'Administrator' : 'Participant');

    const avatarEls = document.querySelectorAll('.user-display-avatar');
    avatarEls.forEach(el => {
      const initials = user.fullName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
      el.textContent = initials || 'U';
    });

    // Show/hide role-specific elements
    document.querySelectorAll('.admin-only').forEach(el => {
      el.style.display = user.role === 'ADMIN' ? '' : 'none';
    });
    document.querySelectorAll('.participant-only').forEach(el => {
      el.style.display = user.role === 'PARTICIPANT' ? '' : 'none';
    });
  },

  async login(email, password) {
    const user = await API.post('/api/auth/login', { email, password });
    this.currentUser = user;
    showToast('Welcome back, ' + user.fullName + '!', 'success');
    const urlParams = new URLSearchParams(window.location.search);
    const redirectUrl = urlParams.get('redirect');
    setTimeout(() => {
      if (redirectUrl) {
        window.location.href = redirectUrl;
      } else if (user.role === 'ADMIN') {
        window.location.href = 'admin-dashboard.html';
      } else {
        window.location.href = 'participant-dashboard.html';
      }
    }, 600);
    return user;
  },

  async register(data) {
    const user = await API.post('/api/auth/register', data);
    this.currentUser = user;
    showToast('Registration successful! Welcome to TRIX Workshops.', 'success');
    const urlParams = new URLSearchParams(window.location.search);
    const redirectUrl = urlParams.get('redirect');
    setTimeout(() => {
      if (redirectUrl) {
        window.location.href = redirectUrl;
      } else {
        window.location.href = 'participant-dashboard.html';
      }
    }, 600);
    return user;
  },

  async logout() {
    try {
      await API.post('/api/auth/logout');
    } catch (ignored) {}
    this.currentUser = null;
    showToast('Logged out successfully', 'info');
    setTimeout(() => {
      window.location.href = 'login.html';
    }, 400);
  }
};

