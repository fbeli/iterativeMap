
let mediaRecord;
$(function(){


    navigator.mediaDevices
        .getUserMedia({audio:true})
        .then( stream => {
            mediaRecord = new MediaRecorder(stream);
            let chunks = []
            mediaRecord.ondataavailable = data => {
                console.log(data);
                chunks.push(data.data);
            }
            mediaRecord.onstop = () => {
                console.log("stopped media record");
                const blob = new Blob(chunks, {type: 'audio/ogg code=opus'})
                const reader = new FileReader();
                reader.readAsDataURL(blob)
                reader.onloadend = function(){
                    document.getElementById("cadastro_audio").value =  reader.result;
                }
            }
        }, err => {
            alert("please allow audio record");
        })



})

function gravar()
{
    let opt = document.getElementById("btn_gravar").textContent;
    if( opt == 'Start Recorder'){
        mediaRecord.start();
        document.getElementById("btn_gravar").textContent = "Stop";
    }else{
        mediaRecord.stop();
        document.getElementById("btn_gravar").textContent = "Start Recorder";
    }

}
