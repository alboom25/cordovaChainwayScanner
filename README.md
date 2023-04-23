# cordovaChainwayScanner
This plugin enable directly scan 1d/2d codes into the cordova application.
#This plugin has been tested with Chainway c75 https://www.chainway.net/Products/Info/45
# Install the plugin
# Initialize the scanner by:

cordova.plugins.chainScanner.startListening(function(strSuccess) {
    //console.log(strSuccess);
}, function(strError) {
    alert(strError);
});  


# the above code initializes the plugin then waits for a scanned code
# Receive and handle the code by the following code
document.addEventListener('onBarcodeScanned', function(data) {     
   alert(data.barcode_data); 
});

# For any assistance contact alboom25@gmail.com or +254711223639
