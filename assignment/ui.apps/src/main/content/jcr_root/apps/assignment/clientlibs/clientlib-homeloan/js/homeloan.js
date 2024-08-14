document.getElementById('homeLoanForm').addEventListener('submit', function (e) {
    e.preventDefault();

    // Clear previous results
    document.getElementById('loanResult').innerHTML = '';

    // Fetch form data
    const clientName = document.getElementById('clientName').value.trim();
    const clientIncome = document.getElementById('clientIncome').value.trim();
    const loanAmount = document.getElementById('loanAmount').value.trim();
    const loanTerm = document.getElementById('loanTerm').value.trim();
    const existingEMIs = document.getElementById('existingEMIs').value.trim();
    const interestRate = document.getElementById('interestRate').value.trim();


    $.ajax({
        url: '/content/assignment/us/en/first-blog/jcr:content/root/homeloancalculator.json',
        type: 'POST',
        dataType: 'json',
        data: {
            clientName: clientName,
            clientIncome: clientIncome,
            loanAmount: loanAmount,
            loanTerm: loanTerm,
            existingEMIs: existingEMIs,
            interestRate: interestRate
        },
        success: function (data) {
            console.log(data);
            if (data.eligible) {
                document.getElementById('loanResult').innerHTML = `<p> Client : ${data.clientName} </br> Status: ${data.message} </br> EMI: ${data.emi}</p>`;
            } else {
                document.getElementById('loanResult').innerHTML = `<p> Client : ${data.clientName} </br> Status: ${data.message} </br> EMI: ${data.emi}</p>`;
            }
        },
        error: function () {
            document.getElementById('loanResult').innerHTML = '<p>An error occurred while processing your request. Please try again later.</p>';
        }
    });
});
