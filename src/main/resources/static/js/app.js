document.addEventListener('DOMContentLoaded', function () {
    var quotePrompt = document.getElementById('quotePrompt');
    if (quotePrompt && window.confirm(quotePrompt.getAttribute('data-message'))) {
        window.location.assign(quotePrompt.getAttribute('data-quote-url'));
    }

    document.querySelectorAll('form[data-prevent-double-submit]').forEach(function (form) {
        var submitting = false;
        form.addEventListener('submit', function (event) {
            if (submitting) {
                event.preventDefault();
                return;
            }
            submitting = true;
            var submitButton = event.submitter;
            if (submitButton) {
                submitButton.disabled = true;
                var loadingText = submitButton.getAttribute('data-loading-text');
                if (loadingText) {
                    submitButton.textContent = loadingText;
                }
            }
        });
    });

    document.querySelectorAll('[data-toggle-password]').forEach(function (button) {
        var input = document.getElementById(button.getAttribute('aria-controls'));
        if (!input) {
            return;
        }

        button.addEventListener('click', function () {
            var isPassword = input.type === 'password';
            input.type = isPassword ? 'text' : 'password';
            button.textContent = isPassword
                ? button.getAttribute('data-hide-label')
                : button.getAttribute('data-show-label');
            button.setAttribute('aria-pressed', String(isPassword));
        });
    });

    document.querySelectorAll('form[data-confirm]').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            var message = form.getAttribute('data-confirm');
            if (message && !window.confirm(message)) {
                event.preventDefault();
            }
        });
    });

    document.querySelectorAll('[data-copy-target]').forEach(function (button) {
        button.addEventListener('click', function () {
            var selector = button.getAttribute('data-copy-target');
            var input = document.querySelector(selector);
            if (!input) {
                return;
            }
            copyText(input.value, button);
        });
    });

    document.querySelectorAll('[data-copy]').forEach(function (button) {
        button.addEventListener('click', function () {
            copyText(button.getAttribute('data-copy'), button);
        });
    });

    var printButton = document.getElementById('printButton');
    if (printButton) {
        printButton.addEventListener('click', function () {
            window.print();
        });
    }
});

function copyText(text, button) {
    if (!text) {
        return;
    }
    var done = function () {
        var original = button.textContent;
        button.textContent = 'Copiado';
        window.setTimeout(function () {
            button.textContent = original;
        }, 1600);
    };

    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text).then(done).catch(function () {
            fallbackCopy(text, done);
        });
    } else {
        fallbackCopy(text, done);
    }
}

function fallbackCopy(text, done) {
    var textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.setAttribute('readonly', '');
    textarea.className = 'clipboard-helper';
    document.body.appendChild(textarea);
    textarea.select();
    try {
        document.execCommand('copy');
        done();
    } catch (error) {
        window.prompt('Copiá el enlace manualmente:', text);
    } finally {
        document.body.removeChild(textarea);
    }
}
