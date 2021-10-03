function handleUpload(e) {
    const fileUploadButton = document.getElementById("upload-button");
    const fileUploadField = document.getElementById("uploaded-file");
    const fileName = fileUploadField.files[0].name;
    this.updateFileNameOnFileSubmit(e, fileName);
    fileUploadButton.disabled = fileUploadField.files.length <= 0;
}

function updateFileNameOnFileSubmit(event, filename) {
    console.log(filename);
    const nextSibling = event.target.nextElementSibling;
    nextSibling.innerText = filename;
}

function handleUploadButtonClick() {
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    const uploadedFile = document.getElementById('uploaded-file').files[0];
    formData.append("file", uploadedFile);
    xhr.upload.addEventListener("progress", onUploadFile, false);
    xhr.addEventListener("load", () => onUploadSuccess(uploadedFile.name), false);
    xhr.addEventListener("error", onUploadFail, false);
    xhr.open("POST", "/upload");
    xhr.send(formData);
}

function onUploadFile() {
    console.log("uploading in progress");
    swal({
        title: 'Upload in Progress ....'
    });
}

function onUploadSuccess(uploadedFile) {
    console.log("Upload Success");
    swal({
        title: "Upload Success !",
        text: "File " + uploadedFile + " successfully uploaded to drive",
        icon: "success",
    })
        .then(() => {
            window.location.reload()
        });
}

function onUploadFail() {
    console.log("Upload Failed");
    swal({
        title:"Upload Filed",
        text: "Sorry, could not upload your file",
        icon: "warning"
    }).then(()=>{
        window.location.reload()
    });

}


window.onload = function () {
    document.getElementById("upload-button").disabled = true;
    document.getElementById('uploaded-file').addEventListener('change', ev => handleUpload(ev), false);
    document.getElementById('upload-button').addEventListener('click', ev => handleUploadButtonClick(ev), false);
};
