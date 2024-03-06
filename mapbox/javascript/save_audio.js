
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
        currentTime = undefined;

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
    document.getElementById("btn_gravar").textContent = "Stop -> "+minutes+":"+seconds;

    if (remainingTime <= 0) {
        gravar();
    }
}


let audio_source;
function stop_on_safari(){
    audio_source.stop();
    document.getElementById("play").style.display = "flex";
    document.getElementById("stop").style.display = "none";
}
async function play_on_safari(URL) {

    // Check if the browser supports web audio. Safari wants a prefix.
    if ('AudioContext' in window || 'webkitAudioContext' in window) {

        //////////////////////////////////////////////////
        // Here's the part for just playing an audio file.
        //////////////////////////////////////////////////
        var play = function play(audioBuffer) {
            audio_source = context.createBufferSource();
            audio_source.buffer = audioBuffer;
            audio_source.connect(context.destination);
            audio_source.start();
        };

        //var URL = 'https://s3-us-west-2.amazonaws.com/s.cdpn.io/123941/Yodel_Sound_Effect.mp3';
        //var AudioContext = window.AudioContext || window.webkitAudioContext;
        var AudioContext = window.AudioContext;
        var context = new AudioContext(); // Make it crossbrowser
        var gainNode = context.createGain();
        gainNode.gain.value = 1; // set volume to 100%
        var playButton = document.querySelector('#play');
        var yodelBuffer = void 0;

        // The Promise-based syntax for BaseAudioContext.decodeAudioData() is not supported in Safari(Webkit).
        await window.fetch(URL)
            .then(response => response.arrayBuffer())
            .then(arrayBuffer => context.decodeAudioData(arrayBuffer,
                audioBuffer => {
                    yodelBuffer = audioBuffer;
                },
                error =>
                    console.error(error)
            ))

        document.getElementById("play").style.display = "none";
        document.getElementById("stop").style.display = "flex";

        await play(yodelBuffer);

    }
}
