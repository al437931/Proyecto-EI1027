/**
 * SgOVI - Taules ordenables
 * Permet ordenar qualsevol taula amb la classe 'sortable'
 * fent clic a les capçaleres (th).
 */
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('table.sortable').forEach(function (table) {
        // Crear input de cerca
        var searchContainer = document.createElement('div');
        searchContainer.className = 'mb-2 d-flex align-items-center justify-content-end';
        searchContainer.innerHTML = '<i class="bi bi-search me-2 text-muted"></i><input type="text" class="form-control w-25 min-w-200" placeholder="Cercar a la taula...">';
        
        // Inserir abans de la taula (o del seu contenidor responsive)
        var wrapper = table.closest('.table-responsive');
        if (wrapper) {
            wrapper.parentNode.insertBefore(searchContainer, wrapper);
        } else {
            table.parentNode.insertBefore(searchContainer, table);
        }
        
        var searchInput = searchContainer.querySelector('input');
        searchInput.addEventListener('keyup', function() {
            var term = searchInput.value.toLowerCase();
            var rows = table.querySelectorAll('tbody tr');
            
            rows.forEach(function(row) {
                var text = row.textContent.toLowerCase();
                row.style.display = text.indexOf(term) > -1 ? '' : 'none';
            });
        });

        var headers = table.querySelectorAll('thead th');
        headers.forEach(function (th, index) {
            // No ordenar columnes d'accions
            if (th.classList.contains('no-sort')) return;

            th.addEventListener('click', function () {
                var tbody = table.querySelector('tbody');
                var rows = Array.from(tbody.querySelectorAll('tr'));
                var ascending = !th.classList.contains('sort-asc');

                // Netejar indicadors anteriors
                headers.forEach(function (h) {
                    h.classList.remove('sort-asc', 'sort-desc');
                });

                th.classList.add(ascending ? 'sort-asc' : 'sort-desc');

                rows.sort(function (a, b) {
                    var cellA = a.querySelectorAll('td')[index];
                    var cellB = b.querySelectorAll('td')[index];
                    if (!cellA || !cellB) return 0;

                    var valA = cellA.textContent.trim();
                    var valB = cellB.textContent.trim();

                    // Intent d'ordenar com a número
                    var numA = parseFloat(valA);
                    var numB = parseFloat(valB);
                    if (!isNaN(numA) && !isNaN(numB)) {
                        return ascending ? numA - numB : numB - numA;
                    }

                    // Intent d'ordenar com a data dd/MM/yyyy
                    var dateRegex = /^(\d{2})\/(\d{2})\/(\d{4})$/;
                    var matchA = valA.match(dateRegex);
                    var matchB = valB.match(dateRegex);
                    if (matchA && matchB) {
                        var dA = new Date(matchA[3], matchA[2] - 1, matchA[1]);
                        var dB = new Date(matchB[3], matchB[2] - 1, matchB[1]);
                        return ascending ? dA - dB : dB - dA;
                    }

                    // Ordenar com a text
                    return ascending
                        ? valA.localeCompare(valB, 'ca')
                        : valB.localeCompare(valA, 'ca');
                });

                rows.forEach(function (row) {
                    tbody.appendChild(row);
                });
            });
        });
    });
});
