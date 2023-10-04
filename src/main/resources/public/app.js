app = (function(api){
    let _publicFunctions = {};

    let _renderSearch = function(data) {
        $(document).ready(() => {
            let json = JSON.parse(data);
            console.log(json);
            $("#data tbody").text("");
            for (var key in json) {
                let reg = json[key]
                let row = `<tr><td>${reg["message"]}</td><td>${reg["date"]}</td></tr>`;
                $("#data tbody").append(row);
            }
        });
    }

    let _updatePost = function(data) {
        $(document).ready(() => {
            $("#postrespmsg").html("Se ha guardado el registro");
        });
    }

    _publicFunctions.get = function() {
        api.getLogs(_renderSearch);
    }

    _publicFunctions.post = function(message) {
        api.post(message, _updatePost);
    }

    return _publicFunctions;
})(apiclient);