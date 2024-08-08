document.addEventListener('DOMContentLoaded', function() {
    const publishDateElement = document.getElementById('publish-date');
    const timestamp = publishDateElement.getAttribute('data-timestamp');
    const date = new Date(parseInt(timestamp));

    // Format the date as needed
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    const formattedDate = date.toLocaleDateString('en-US', options);

    // Display the formatted date
    publishDateElement.textContent = `Publish Date: ${formattedDate}`;
});
