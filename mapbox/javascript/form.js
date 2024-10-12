

let accessToken = null;
let userName = null;
let previous_div = null;
let create_point = false;
let zoom_to_create_point = 16.5;
let formattedPhoneNumber;
let zoom = 16;
let link;
let firstTime = true;
let my_instagram;
let pointId;

function afterLogin(){
    fechar_divs();
    configPersonalInfo()

    document.getElementById("welcome_name").innerHTML =  userName;
    if(my_instagram !== undefined || my_instagram !== "" || my_instagram != "@null") {
        link = "https://www.mygmap.com/" + my_instagram.replace("@", "");
        document.getElementById("span_msg").innerHTML = "Now you have your own map <br>" + link +
            "<br><a href='#' onclick='copyLink()' title='Copy'> <img alt='Copy' aria-label='copy' src='img/copy.png' id='copy' style='width:20px'/></a>";
        document.getElementById("span_msg").style.fontSize = "small"
    }
    get_li_after_login(userName);
    save_cookies("token", accessToken);
    save_cookies("name", userName);
    welcome_div_show();
}
function configPersonalInfo(){
    const decode =  decodeURIComponent(atob(accessToken.split('.')[1].replace('-', '+').replace('_', '/')).split('').map(c => `%${('00' + c.charCodeAt(0).toString(16)).slice(-2)}`).join(''));
    //console.log(decode);
    userName = JSON.parse(decode)["nome_completo"];
    my_instagram = JSON.parse(decode)["usuario_instagram"];
}

function copyLink(){
    navigator.clipboard.writeText(link);
}

function copy_value_to_clipboard(value){
    navigator.clipboard.writeText(value);
}

function open_div_login(){
    fechar_divs();
    document.getElementById("sidebar_logout_div").style.display="block";
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
            if(data.status === 200) {
                setToken(data.token);
                afterLogin();
            }else{
               // document.getElementById("erro_alert_text").innerHTML = data.error;
                error_div_event("login_div",data.error);
            }
        })
        .catch(error => {
            error_div_event("login_div", data.error);
        });
}

async function forget_password(){

    let data = {
        email: document.getElementById("forget_login_email").value,
    };
    fetch(config.forget_password, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            if(data.status === 200) {
                fechar_divs();
                document.getElementById("info_info").innerHTML = "An email has set to address, open your email and follow the steps to reset your password";
                document.getElementById("info_title").innerHTML = "Password Reset Requested";
                document.getElementById("info_highlight").innerHTML = "";
                document.getElementById("info_div").style.display = 'flex';


            }else{

                error_div_event("forget_password_div", data.error);
            }
        })
        .catch(error => {
            error_div_event("forget_password_div", data.error);
        });
}

function setToken(receicedToken){
    accessToken = "Bearer "+receicedToken;
}

function validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}
function addPlusIfMissing(str) {
    if (str[0] !== '+') {
        return '+' + str;
    }
    return str;
}
function validateAndFormatPhoneNumber(phoneNumber) {
    const regex = /^\+\d{1,3}[-\.\s]??\d{1,4}[-\.\s]??\d{1,4}[-\.\s]??\d{1,4}$/;
    formattedPhoneNumber = addPlusIfMissing(phoneNumber).replace(/\s/g, '').replace(/-/g, '').replace(/\./g, '');
    return regex.test(formattedPhoneNumber);
}


function form_sign_in_is_ok(){
    let validEmail = validateEmail(document.getElementById("sign_up_email").value);
    if (!validEmail) {
        error_div_event("sign_up_div", "Use valid Email");
        return false;
    }
    if (document.getElementById("sign_up_name").value === 'Name') {
        error_div_event("sign_up_div", "Use valid Name");
        return false;
    }
    if (document.getElementById("sign_up_password").value === 'Password') {
        error_div_event("sign_up_div", "Please, don't use word Password");
        return false;
    }
    formattedPhoneNumber = document.getElementById("sign_up_phone").value;

    if (!validateAndFormatPhoneNumber(formattedPhoneNumber)) {
        error_div_event("sign_up_div", "Use valid Phone Number");
        return false;
    }
    if (document.getElementById("sign_up_country").value === 'Country') {
        error_div_event("sign_up_div", "Please select Country");
        return false;
    }
    return true;
}

function execute_sign_in(){

    if( ! form_sign_in_is_ok())
        return;
    let data = {
        email: document.getElementById("sign_up_email").value,
        password: document.getElementById("sign_up_password").value,
        country: document.getElementById("sign_up_country").value,
        phone: document.getElementById("sign_up_phone").value,
        name: document.getElementById("sign_up_name").value,
        born_date: document.getElementById("sign_up_born_date").value,
        instagram: document.getElementById("sign_up_instagram").value,
       /* share: document.getElementById("sign_up_share").value,
        guide: document.getElementById("sign_up_guide").value,*/
        description: document.getElementById("sign_up_description").value

    };

    try {
        fetch(config.sign_in_url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(data => {
                console.log(data);
                if (data.status === 200) {
                    setToken(data.token);
                    afterLogin();
                } else {
                    if (data.status === 409) {
                        error_div_event("sign_up_div", "Email already registered.");
                    } else {
                        error_div_event("sign_up_div", "Error to sign in, try again");
                    }
                }
            })
            .catch(error => {
                console.log(error);
                error_div_event("sign_up_div", "Erro to connect, try again latter");
            });
    }catch (e) {
        console.log(e);
        error_div_event("sign_up_div", "Erro to connect, try again latter");
    }
}

function logout(){

    accessToken = null;
    document.getElementById("li_login_a_link").innerHTML = "Login";
    document.getElementById("sidebar_login_div").setAttribute("onclick", "login()");
    fechar_divs();

}
function form_cadastro_is_ok(){
    if(document.getElementById("cadastro_language").value === 'Language'){
        error_div_event("cadastro_div", "Select a language");
        return false ;
    }
    if(document.getElementById("cadastro_titulo").value === 'Title'){
        error_div_event("cadastro_div", "Insert Title");
        return false ;
    }

    if(document.getElementById("cadastro_description").value === 'Description'){
        document.getElementById("cadastro_description").value = document.getElementById("cadastro_titulo").value;
    }
    if(document.getElementById("cadastro_place_type").value === 'Place Type'){
        error_div_event("cadastro_div", "Insert Place Type");
        return false ;
    }

    return true;
}

async function create_new_point() {

    // teste se ainda está gravando
    if (recording) {
        gravar();
    }
    document.getElementById("btn_criar_ponto").innerHTML = "Saving Point - Wait";

    let fileInput = document.getElementById('cadastro_img') ;
    let audioInput = document.getElementById('cadastro_audio_upload') ;
    if (!form_cadastro_is_ok())
        return;
    let data = {
        longitude: document.getElementById("cadastro_lng").value,
        latitude: document.getElementById("cadastro_lat").value,
        title: document.getElementById("cadastro_titulo").value,
        description: document.getElementById("cadastro_description").value,
        audio: document.getElementById("cadastro_audio").value,
        language: document.getElementById("cadastro_language").value,
        type: document.getElementById("cadastro_place_type").value
    };
    try {

        const response = await fetch(config.cadastro_url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json;charset=UTF-8",
                "Authorization": accessToken
            },
            body: JSON.stringify(data)
        });

        await response.json()
            .then(data => {

                if (data.status === "200") {
                    if(fileInput !== null && fileInput.files[0] !== undefined){
                        upload_point_photo(data.pointId, fileInput)
                    }
                    if(audioInput !== null && audioInput.files[0] !== undefined){
                        upload_point_photo(data.pointId, audioInput)
                    }
                    console.log(data.pointId +" "+ document.getElementById("cadastro_titulo").value);
                    point_link = "https://www.guidemapper.com/?point="+data.pointId;
                    fechar_divs();
                    document.getElementById("info_info").innerHTML = "Press <b>here</b> to copy point link and share it";
                    document.getElementById("info_title").innerHTML = "New point added";
                    document.getElementById("info_highlight").innerHTML = "";
                    document.getElementById("info_div").style.display = 'flex';
                    document.getElementById("cadastro_titulo").value = 'Title';
                    document.getElementById("cadastro_description").value = 'Description';
                    document.getElementById("cadastro_audio").value = "";
                    document.getElementById("btn_cadastro_img").innerHTML = "Select Picture";
                    document.getElementById("btn_criar_ponto").innerHTML = "Create Point";
                    //document.getElementById("cadastro_img").value = null;

                } else {
                    error_div_event("cadastro_div", "Erro to create new point.Check data and try again.");
                }

            })
            .catch(error => {
                console.log(error);
                alert("Erro to connect, try again.");
            });
    } catch (e) {
        console.log(e.value);
    }
}

function clearOnFocus(fiel_id, defaul_value){
    field_value = document.getElementById(fiel_id).value;
    if(field_value == defaul_value){
        document.getElementById(fiel_id).value = "";
    }
}
function onExit(fiel_id, defaul_value){
    field_value = document.getElementById(fiel_id).value;
    if(field_value == ""){
        document.getElementById(fiel_id).value = defaul_value;
    }
}

async function uploadClick(fileInputName, fileInputButtonName){
    document.getElementById(fileInputName).click();
    let new_str;
    if(fileInputButtonName === "btn_upload_audio")
        new_str = "Another Audio"
    else
        new_str = "Another Photo"
    document.getElementById(fileInputButtonName).innerHTML = new_str;

}
