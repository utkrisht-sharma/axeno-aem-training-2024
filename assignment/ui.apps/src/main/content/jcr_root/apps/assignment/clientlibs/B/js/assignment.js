// bloglisting.js

document.addEventListener('DOMContentLoaded', function() {
    // Toggle the visibility of blog details
    const blogItems = document.querySelectorAll('.blog-listing li');
    
    blogItems.forEach(function(item) {
        const title = item.querySelector('h2');
        const details = item.querySelectorAll('p');
        
        title.style.cursor = 'pointer';
        details.forEach(detail => detail.style.display = 'none');
        
        title.addEventListener('click', function() {
            details.forEach(function(detail) {
                if (detail.style.display === 'none') {
                    detail.style.display = 'block';
                } else {
                    detail.style.display = 'none';
                }
            });
        });
    });

    // Log blog titles to the console
    blogItems.forEach(function(item) {
        const title = item.querySelector('h2').textContent;
        console.log(`Blog title: ${title}`);
    });
});
