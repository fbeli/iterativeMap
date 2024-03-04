
let mediaRecord;
let recording = false;

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

    if(recording){
        recording = false;
        mediaRecord.stop();
        clearInterval(countdownTimer);
        document.getElementById("btn_gravar").textContent = "Start Recorder";

    } else {
        mediaRecord.start();
        countdownTimer = setInterval(updateCountdown, 3000);
       // document.getElementById("btn_gravar").textContent = "Stop";
        recording = true;
    }

}
let currentTime;
let countdownTimer

// Função para atualizar o contador de tempo
function updateCountdown() {


    if (currentTime === undefined){
        currentTime = new Date();
    }

    const endTime = new Date(currentTime);
    endTime.setMinutes(currentTime.getMinutes() + 1);
    endTime.setSeconds(currentTime.getSeconds() + 30);

    const remainingTime = Math.max(0, (endTime - new Date()) / 1000);

    const minutes = Math.floor(remainingTime / 60);
    let seconds = Math.floor(remainingTime % 60);

    if (seconds < 10)
        seconds = '0' + seconds;
   // console.log(`Tempo restante: ${minutes} minutos e ${seconds} segundos`);
    document.getElementById("btn_gravar").textContent = "Stop -> "+minutes+":"+seconds;

    if (remainingTime <= 0) {
        gravar();
    }
}

// Iniciar o contador de tempo

