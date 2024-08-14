

                $('#homeLoanForm').submit(function(event) {
          event.preventDefault(); // Prevent the form from submitting the traditional way

          $.ajax({
              url: '/content/assignment/us/blog-details/jcr:content/root/emicalculator.json',
              type: 'POST',
              dataType: 'json',
              // Specify content type for form data
              data: $(this).serialize(), // Serialize form data

              success: function(response) {
                  // Handle success
                  console.log('Response received:', response);
                  alert('Response received: ' + JSON.stringify(response));
              },

              error: function(xhr, status, error) {
                  // Handle error
                  console.error('Error:', status, error);
                  alert('An error occurred: ' + status + ' - ' + error);
              }
          });
      });


