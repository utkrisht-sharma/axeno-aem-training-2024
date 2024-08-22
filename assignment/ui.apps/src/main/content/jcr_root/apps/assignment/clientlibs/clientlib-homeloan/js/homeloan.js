document.querySelector('.form').addEventListener('submit', function (e) {
    e.preventDefault();

    // Clear previous results
    document.querySelector('.result').innerHTML = '';

    // Fetch form data
    const clientName = document.querySelector('.client-name').value.trim();
    const clientIncome = document.querySelector('.client-income').value.trim();
    const loanAmount = document.querySelector('.loan-amount').value.trim();
    const loanTerm = document.querySelector('.loan-term').value.trim();
    const existingEMIs = document.querySelector('.existing-emis').value.trim();
    const interestRate = document.querySelector('.interest-rate').value.trim();

    // Retrieve the resource path from the data attribute
    const resourcePath = document.querySelector('.form-container').dataset.resourcePath;

    $.ajax({
        url: `${resourcePath}.homeloancalculator.json`,
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
            document.querySelector('.result').innerHTML = `<p> Client : ${data.clientName} </br> Status: ${data.message} </br> EMI: ${data.emi}</p>`;
        },
        error: function () {
            document.querySelector('.result').innerHTML = '<p>An error occurred while processing your request. Please try again later.</p>';
        }
    });
});
