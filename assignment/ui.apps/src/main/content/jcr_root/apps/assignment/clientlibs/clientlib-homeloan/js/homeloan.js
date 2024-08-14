$(document).ready(function () {

    $("#homeLoanForm").on("submit", function (event) {

        event.preventDefault(); // Prevent the default form submission


        var formData = {
            clientName: $("#clientName").val(),
            clientIncome: $("#clientIncome").val(),
            loanAmount: $("#loanAmount").val(),
            loanTerm: $("#loanTerm").val(),
            existingEMIs: $("#existingEMIs").val(),
            interestRate: $("#interestRate").val()
        };


        $.ajax({
            type: "POST",
            url: "http://localhost:4502//content/assignment/homeloanpage/jcr:content/root/container/homeloaneligibilityf.json",
            dataType: "json",
            data:formData,
            success: function (response) {
                if(response.invalidField != null){
                alert("Invalid Data Entered")
                }
                else{
                if(response.eligible){
                alert("Your Loan Is Approved And Your Monthly Emi Is - "+response.emi);
                }
                else{
                alert("Your Loan Is Not Approved. ");
                }

                }
            },
            error: function (xhr, status, error) {
                // Handle any errors
                alert("An error occurred: " + error);
            }
        });
    });
});
