apiclient = (function() {

    let _publicFunctions = {};


    _publicFunctions.getLogs = function (callback) {
        return $.get(`/logservice`, (data) => {
                    callback(data);
                }).fail();
    }

    _publicFunctions.post = function (message, callback) {
        return $.ajax({
                url: `/logservice`,
                type: 'POST',
                data: JSON.stringify({
                    message: message
                }),
                contentType: "application/json",
                success: data => callback(data),
            });
    };

    return _publicFunctions;
})()