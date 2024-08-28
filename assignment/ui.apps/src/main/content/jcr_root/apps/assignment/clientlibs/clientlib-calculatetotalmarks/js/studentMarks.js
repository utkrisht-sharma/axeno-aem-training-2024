$(document).ready(function() {
    $('#addButton').click(function() {
        var marks = [];
        $('.subject-marks').each(function() {
            var mark = $(this).text();
            marks.push(mark);
        });

        if (marks.length === 0) {
            alert('No student marks available to add. Please add student details in the multifield.');
            return;
        }

        // Make an AJAX call to the servlet
        $.ajax({
            url: '/bin/calculateTotalMarks',
            type: 'POST',
            data: { marks: marks.join(',') },
            success: function(data) {
                $('#totalMarksResult').text('Total Marks: ' + data.totalMarks);
            },
            error: function(xhr, status, error) {
                $('#totalMarksResult').text('Error fetching total marks.');
            }
        });
    });
});
