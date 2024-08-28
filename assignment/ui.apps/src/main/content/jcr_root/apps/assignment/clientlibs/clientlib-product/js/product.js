document.getElementById("addToCart").addEventListener("click", function() {
      var quantity = document.querySelector(".quantity-select").value;
      var productType = document.querySelector(".product-name").dataset.productType;

      jQuery.ajax({
           url: "/bin/addToCart",
           dataType: 'json',
           type: "POST",
           data: {
                quantity: quantity,
                productType: productType
            },
            success: function(response) {
                if (response.message) {
                     alert(response.message);
                 }else{
                    alert(response.error);
                 }
            },
             error: function(xhr, status, error) {
                    alert("An error occurred: " + response.error);
             }
        });
    });

