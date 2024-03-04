

let accessToken = null;
let previous_div = null;
let create_point = false;
let zoom_to_create_point = 16.5;
let zoom = 10;

function afterLogin(){
    fechar_divs();

    const decode =  decodeURIComponent(atob(accessToken.split('.')[1].replace('-', '+').replace('_', '/')).split('').map(c => `%${('00' + c.charCodeAt(0).toString(16)).slice(-2)}`).join(''));
    //console.log(decode);
    let nome = JSON.parse(decode)["nome_completo"]
    document.getElementById("welcome_name").innerHTML =  nome;
    get_li_after_login(nome);
    welcome_div_show();
}
function get_li_after_login(nome){
    if(nome.indexOf(' ') >= 0){
        nome = nome.split(" ")[0];
    }

    document.getElementById("li_login_a_link").innerHTML = nome ;
    document.getElementById("li_login_a_link").setAttribute("onclick", "login_opt_ul_show()");

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
            }else{
                document.getElementById("erro_alert_text").innerHTML = data.error;
                error_div_event("login_div");
            }
        })
        .catch(error => {
            document.getElementById("erro_alert_text").innerHTML = data.error;
            error_div_event("login_div");
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
let formattedPhoneNumber;

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
        share: document.getElementById("sign_up_share").value,
        guide: document.getElementById("sign_up_guide").value,
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
    document.getElementById("li_login_a_link").setAttribute("onclick", "login()");
    document.getElementById("login_opt_ul").style.display = 'none';

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
        error_div_event("cadastro_div", "Insert Description");
        return false ;
    }
    return true;
}

async function create_new_point() {

    // teste se ainda está gravando
    if (document.getElementById("btn_gravar").textContent === "Stop") {
        gravar();
    }

    if (!form_cadastro_is_ok())
        return;
    let data = {
        longitude: document.getElementById("cadastro_lng").value,
        latitude: document.getElementById("cadastro_lat").value,
        title: document.getElementById("cadastro_titulo").value,
        description: document.getElementById("cadastro_description").value,
        audio: document.getElementById("cadastro_audio").value,
        language: document.getElementById("cadastro_language").value
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
                    fechar_divs();
                    document.getElementById("info_info").innerHTML = "This point will be reviwed and add after it. It" +
                        " can take some minutes, hours or day";
                    document.getElementById("info_title").innerHTML = "New point added";
                    document.getElementById("info_highlight").innerHTML = "";
                    document.getElementById("info_div").style.display = 'flex';
                    document.getElementById("cadastro_titulo").value = 'Title';
                    document.getElementById("cadastro_description").value = 'Description';
                    document.getElementById("cadastro_audio").value = "";

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