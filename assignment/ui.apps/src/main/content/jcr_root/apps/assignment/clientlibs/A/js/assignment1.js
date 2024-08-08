// blogpage.js

document.addEventListener('DOMContentLoaded', function() {
    // Toggle the display of the blog content
    const contentToggleBtn = document.createElement('button');
    contentToggleBtn.textContent = 'Toggle Content';
    contentToggleBtn.style.marginBottom = '20px';
    document.querySelector('.blog-content').before(contentToggleBtn);

    contentToggleBtn.addEventListener('click', function() {
        const blogContent = document.querySelector('.blog-content');
        if (blogContent.style.display === 'none' || blogContent.style.display === '') {
            blogContent.style.display = 'block';
            contentToggleBtn.textContent = 'Hide Content';
        } else {
            blogContent.style.display = 'none';
            contentToggleBtn.textContent = 'Show Content';
        }
    });

    // Show an alert with the author information
    const authorAlertBtn = document.createElement('button');
    authorAlertBtn.textContent = 'Show Author Info';
    authorAlertBtn.style.marginBottom = '20px';
    contentToggleBtn.after(authorAlertBtn);

    authorAlertBtn.addEventListener('click', function() {
        const authorName = document.querySelector('p:contains("Author:")').textContent;
        const authorDesignation = document.querySelector('p:contains("Designation:")').textContent;
        alert(`${authorName}\n${authorDesignation}`);
    });

    // Log the publish date to the console
    const publishDate = document.querySelector('p:contains("Publish Date:")').textContent;
    console.log(publishDate);
});
