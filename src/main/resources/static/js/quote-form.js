document.addEventListener('DOMContentLoaded', function () {
    var form = document.getElementById('quoteForm');
    if (!form) {
        return;
    }

    var tbody = document.getElementById('itemsBody');
    var addButton = document.getElementById('addItem');
    var taxInput = document.getElementById('taxRate');
    var numberFormat = new Intl.NumberFormat('es-AR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });

    function parseNumber(value) {
        var parsed = parseFloat(String(value || '').replace(',', '.'));
        return Number.isFinite(parsed) ? parsed : 0;
    }

    function recalculate() {
        var rows = tbody.querySelectorAll('tr');
        var subtotal = 0;

        rows.forEach(function (row) {
            var quantityInput = row.querySelector('input[name$=".quantity"]');
            var priceInput = row.querySelector('input[name$=".unitPrice"]');
            var lineTotalCell = row.querySelector('[data-line-total]');
            var quantity = parseNumber(quantityInput ? quantityInput.value : 0);
            var price = parseNumber(priceInput ? priceInput.value : 0);
            var lineTotal = quantity * price;

            if (quantity > 0 && price > 0) {
                subtotal += lineTotal;
            }
            if (lineTotalCell) {
                lineTotalCell.textContent = numberFormat.format(lineTotal);
            }
        });

        var taxRate = parseNumber(taxInput ? taxInput.value : 0);
        var tax = subtotal * (taxRate / 100);
        var total = subtotal + tax;

        setText('[data-subtotal]', numberFormat.format(subtotal));
        setText('[data-tax]', numberFormat.format(tax));
        setText('[data-total]', numberFormat.format(total));
        setText('#taxRateLabel', String(taxRate));
    }

    function setText(selector, value) {
        var element = document.querySelector(selector);
        if (element) {
            element.textContent = value;
        }
    }

    function reindexRows() {
        tbody.querySelectorAll('tr').forEach(function (row, index) {
            row.querySelectorAll('input').forEach(function (input) {
                input.name = input.name.replace(/items\[\d+\]/, 'items[' + index + ']');
                input.removeAttribute('id');
            });
        });
    }

    function addRow() {
        var rows = tbody.querySelectorAll('tr');
        if (rows.length === 0) {
            return;
        }
        var clone = rows[rows.length - 1].cloneNode(true);
        clone.querySelectorAll('input').forEach(function (input) {
            var name = input.name || '';
            input.removeAttribute('id');
            if (name.endsWith('.quantity')) {
                input.value = '1';
            } else {
                input.value = '';
            }
        });
        var lineTotal = clone.querySelector('[data-line-total]');
        if (lineTotal) {
            lineTotal.textContent = '—';
        }
        tbody.appendChild(clone);
        reindexRows();
        recalculate();
    }

    function removeRow(button) {
        var row = button.closest('tr');
        if (!row) {
            return;
        }
        var rows = tbody.querySelectorAll('tr');
        if (rows.length > 1) {
            row.remove();
        } else {
            row.querySelectorAll('input').forEach(function (input) {
                if ((input.name || '').endsWith('.quantity')) {
                    input.value = '1';
                } else {
                    input.value = '';
                }
            });
        }
        reindexRows();
        recalculate();
    }

    if (addButton) {
        addButton.addEventListener('click', addRow);
    }

    tbody.addEventListener('click', function (event) {
        var button = event.target.closest('.remove-item');
        if (button) {
            removeRow(button);
        }
    });

    form.addEventListener('input', recalculate);
    recalculate();
});
