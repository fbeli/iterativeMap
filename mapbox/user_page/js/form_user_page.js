
function init() {
    read_cookies();
    if(accessToken !== null){
        afterLogin();
    }
}

function login() {
    show_div("login_div");
}

function show_div(el) {
    fechar_divs();
    document.getElementById(el).style.display = 'block';
}

function fechar_divs() {
    document.getElementById("login_div").style.display = 'none';
    document.getElementById("after_login_div").style.display = 'none';
}

function afterLogin() {
    fechar_divs();
    show_div("after_login_div");
    ler_pontos_usuario();
}

function execute_login() {

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
            if (data.status == "200") {
                setToken(data.token);
                afterLogin();

            } else {
                document.getElementById("erro_login").innerHTML = data.error;
                afterLogin();
            }
        })
        .catch(error => {
            console.log(error);
            alert(error);
        });
}

function setToken(receicedToken) {
    accessToken = "Bearer " + receicedToken;
    split_token(receicedToken);
    save_cookies("token", accessToken);
}
    let tokenPayload;


    function split_token() {
        let arrayToken = accessToken.split('.');
        tokenPayload = JSON.parse(atob(arrayToken[1]));
        user_id = tokenPayload.usuario_id;
        save_cookies("user_id", user_id);
    }

    let page = 0;

    function ler_proxima_pagina(){
        ler_pontos_usuario(++page);
    }
    function ler_pagina_anterior(){
        if(page > 0) {
            ler_pontos_usuario(page - 1);
            page--;
        }
    }

    function ler_pontos_usuario(page) {
        let url;
        if(page != undefined){
            url = config.list_points_by_user + user_id+"&page="+page;
            //confirm_button_ler_pontos_usuario(page);
        }else {
            url = config.list_points_by_user + user_id;
            //confirm_button_ler_pontos_usuario(0);
        }
        fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": accessToken
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.length > 0) {
                    const tableBody = document.getElementById('point');
                    for (i =0; i<tableBody.childNodes.length; i++){
                        tableBody.removeChild(tableBody.childNodes[i]);
                    }
                    data.forEach((item) => {
                        let src_photo=""
                        if(item.photo!=" " && item.photo!=null)
                            src_photo = 'https://guidemapper.com/' + item.photo;
                        let src_audio = 'https://guidemapper.com/' + item.audio;
                        let language = "lang_"+item.pointId;
                        let description = "description_"+item.pointId;
                        let title = "title_"+item.pointId;

                        const row = document.createElement('tr');
                        row.innerHTML = `
                    <td>
                        Update: <img  style="height: 30px" src="update.png" onclick="update_point('${item.pointId}' )">
                         
                         <p> Translate to: <select name="${language}" id="${language}">
                          <option value="">Choose...</option>
                          <option value="PT">PT</option>
                          <option value="SP">SP</option>
                          <option value="EN">EN</option>
                        </select>
                        <img  style="height: 30px" src="update.png" onclick="translate_point('${item.pointId}' )">
                        </p>
                        
                    </td>
                    <td><input type="text" id="${title}" value="${item.title}"></td>
                    <td>${item.aproved}</td>
                    <td>
                        <select name="${language}" id="${language}">
                          <option value="${item.language}">${item.language}</option>
                          <option value="PT">PT</option>
                          <option value="SP">SP</option>
                          <option value="EN">EN</option>
                        </select>
         
                    </td>
                    <td> <textarea cols="20" id="${description}">${item.description}</textarea></td>
                    <td><p>${item.country}</p><p> ${item.state}</p><p> ${item.city}</p></td>
                    <td>
                        <div style="height: 200px; width: 200px">
                            <img style="width: 85%;height: 85%;object-fit:contain" src=${src_photo} alt="${item.title}">
                            <button type="button" id="add_imagem_bt_${item.pointId}" onclick="uploadClick('add_imagem_${item.pointId}', 'add_imagem_bt_${item.pointId}', '${item.pointId}')" class="btn btn-primary rounded submit p-1 px-5" style="margin: 1px 1px 1px 15px;">Upload</button>
                            <input class="file_out" style="display:none" type="file" id="add_imagem_${item.pointId}" name="cadastro_img" value='Add Picture' >
                        </div>
                    </td>
                    <td>
                        <div>
                            <audio src=${src_audio} controls style="max-width: 110px"></audio>
                            <div><button type="button" id="add_audio_bt_${item.pointId}" onclick="uploadClick('add_audio_${item.pointId}', 'add_audio_bt_${item.pointId}', '${item.pointId}')" class="btn btn-primary rounded submit p-1 px-5" style="margin: 1px 1px 1px 15px;">Upload</button></div>
                            <input class="file_out" style="display:none" type="file" id="add_audio_${item.pointId}" name="cadastro_img" value='Add Audio' >
                    </div>
                    </td>
  `;

                        tableBody.appendChild(row);
                    });
                } else {
                    console.log(data.error);
                }
            })
            .catch(error => {
                console.log(error);
                alert(error);
            });


}
function get_li_after_login(nome) {


}

async function uploadClick(fileInputName, fileInputButtonName, point_id){
    document.getElementById(fileInputName).click();
    fileInput = document.getElementById(fileInputName) ;
    upload_point_photo(point_id, fileInput)
    document.getElementById(fileInputButtonName).innerHTML = "Added";

}
async function update_point(point_id){
    let title = document.getElementById("title_"+point_id).value;
    let language = document.getElementById("lang_"+point_id).value;
    let description = document.getElementById("description_"+point_id).value;

    let data = {
        title: title,
        language: language,
        description: description

    };
    fetch(config.update_point + point_id, {
        method: "PUT",
        headers: {
            "Authorization": accessToken,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            if (data.status == "200") {
                alert("Point updated");
            } else {
                alert(data.error);
            }
        })
        .catch(error => {
            console.log(error);
            alert(error);
        });
}
