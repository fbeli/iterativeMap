

let accessToken = null;
let previous_div = null;
let create_point = false;
let zoom_to_create_point = 16.5;
let zoom = 10;

function afterLogin(){
    document.getElementById("after_login_div").style.display = 'block';
}



function fechar_divs(){
    document.getElementById("login_div").style.display = 'none';   
}



function execute_login(){

    let data = {
        email: document.getElementById("login_email").value,
        password: document.getElementById("login_password").value
    };
    fetch(config.login_url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            if(data.status == "200") {
                setToken(data.token);
                afterLogin();
                fechar_divs();
            }else{
                document.getElementById("div_message").innerHTML = data.error;
                afterLogin();
                
            }
        })
        .catch(error => {
           console.log(error);
           alert(error);
        });
}


function gerar_arquivo(tipo){

    let data = {
        email: document.getElementById("login_email").value,
        password: document.getElementById("login_password").value
    };
    if(tipo == "geo"){
        link =  config.gerar_geo_file_url;
    }   else{
        link = config.gerar_arquivo_to_aprove_url
    }
    fetch(link, {
        method: "GET",
        headers: {
            "Content-Type": "application/json;charset=UTF-8",
            "Authorization": accessToken
        }
    })
        .then(response => response.json())
        .then(data => {
            if(data.status == "200") {
                document.getElementById("div_message").innerHTML = data.message;
            }else{
                document.getElementById("div_message").innerHTML = data.error;
                
            }
        })
        .catch(error => {
           console.log(error);
           document.getElementById("div_message").innerHTML = data.error;
          
        });
}

function setToken(receicedToken){
    accessToken = "Bearer "+receicedToken;

}

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
                    //console.log(reader.result);
                    const audio = document.createElement("cadastro_audio");
                    audio.src = reader.result
                }
            }
        }, err => {
            alert("please allow audio record");
        })



})

function gravar()
{
    let opt = document.getElementById("btn_gravar").textContent;
    if( opt == 'Gravar'){
        mediaRecord.start();
        document.getElementById("btn_gravar").textContent = "Stop";
    }else{
        mediaRecord.stop();
        document.getElementById("btn_gravar").textContent = "Gravar";
    }

}
