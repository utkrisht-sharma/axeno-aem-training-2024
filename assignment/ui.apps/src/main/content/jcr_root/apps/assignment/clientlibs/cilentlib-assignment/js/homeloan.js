$(document).ready(function() {
    $('#home-loan-form').submit(function(event) {
        event.preventDefault(); // Prevent the default form submission

        // Validate that numeric values are non-negative
        let clientIncome = parseFloat($('#clientIncome').val());
        let loanAmount = parseFloat($('#loanAmount').val());
        let existingEMIs = parseFloat($('#existingEMIs').val());

        if (clientIncome < 0 || loanAmount < 0 || existingEMIs < 0) {
            $('#loan-result').html('<p>Invalid input: values for income, loan amount, and existing EMIs must be non-negative.</p>');
            return; // Exit the function if validation fails
        }

        // Send an AJAX request to submit the form data
        $.ajax({
            url: '/content/assignment/us/hiiiii/jcr:content/root/container/container/homeloan.json',
            type: 'POST',
            data: $(this).serialize(), // Use serialize() to prepare form data

            dataType: 'json',
            success: function(data) {
                let result = '<h3>Loan Application Result</h3>';
                result += `<p>Client Name: ${data.clientName}</p>`;
                result += `<p>Status: ${data.eligible ? 'Approved' : 'Rejected'}</p>`;
                if (data.eligible) {
                    result += `<p>EMI: ${data.emi}</p>`;
                }
                result += `<p>Message: ${data.message}</p>`;
                $('#loan-result').html(result);
            },
            error: function(xhr, status, error) {
                $('#loan-result').html('<p>Error processing the loan application.</p>');
                console.error('Error:', error);
            }
        });
    });
});
