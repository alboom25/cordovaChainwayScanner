var exec = require('cordova/exec');

var chainScanner = {
	startListening: function (successCallback, failureCallback) {
		var onSuccessCallback = function (barcode_data) {    
      cordova.fireDocumentEvent('onBarcodeScanned', {barcode_data: barcode_data});	
      successCallback(barcode_data);		
		};
		exec(onSuccessCallback, failureCallback, 'chainScanner', 'startListening', []);
	},
	stopWatch: function (successCallback, failureCallback) {
		exec(successCallback, failureCallback, 'chainScanner', 'stopListening', []);
	}
};

module.exports = chainScanner;
