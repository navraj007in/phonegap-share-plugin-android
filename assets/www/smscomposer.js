var composer = {
    showComposer: function(title, message, successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'SMSComposer', // mapped to our native Java class called "Calendar"
            'showSMSComposer', // with this action name
            [{                  // and this array of custom arguments to create our entry
                "title": title,
                "message": message,
            }]
        );
     }
}