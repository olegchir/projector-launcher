document.querySelector('#connect-button').addEventListener('click', function () {

    let url = document.getElementById("url-text-field").value;
    const {ipcRenderer} = require('electron')
    ipcRenderer.send("connect", url);

});
