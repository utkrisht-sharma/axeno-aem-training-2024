$(document).ready(function() {
    $('#compareButton').on('click', function() {
        $.ajax({
            url: '/apps/assignment/components/studentDetails',
            type: 'GET',
            success: function(data) {
                $('#result').text(data);
            },
            error: function(xhr, status, error) {
                console.error('AJAX request failed:', status, error);
                $('#result').text('Failed to retrieve student details.');
            }
        });
    });
});
