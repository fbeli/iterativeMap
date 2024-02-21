

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
function execute_sign_in(){

    let data = {
        email: document.getElementById("sign_up_email").value,
        password: document.getElementById("sign_up_password").value,
        country: document.getElementById("sign_up_country").value,
        phone: document.getElementById("sign_up_phone").value,
        name: document.getElementById("sign_up_name").value,

    };

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
            if (data.status == "200") {
                setToken(data.token);
                afterLogin();
            }else{
            if (data.status == "409") {
                document.getElementById("erro_alert_text").innerHTML = "Email already registered.";
                error_div_event("sign_up_div");
            } else {
                alert("Erro to sign in.");
            }
        }
        })
        .catch(error => {
            console.log(error);
            alert("Erro to connect, try again.");
        });

}
function logout(){
    accessToken = null;
    document.getElementById("li_login_a_link").innerHTML = "Login";
    document.getElementById("li_login_a_link").setAttribute("onclick", "login()");
    document.getElementById("login_opt_ul").style.display = 'none';

}
function create_new_point(){
    let data = {
        longitude: document.getElementById("cadastro_lng").value,
        latitude: document.getElementById("cadastro_lat").value,
        title: document.getElementById("cadastro_titulo").value,
        description: document.getElementById("cadastro_description").value,
        audio: document.getElementById("cadastro_audio").value
    };

    fetch(config.cadastro_url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json;charset=UTF-8",
            "Authorization": accessToken
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            if(data.status == "200") {
                fechar_divs();
                document.getElementById("info_info").innerHTML = "This point will be reviwed and add after it. It" +
                    " can take some minutes, hours or day";
                document.getElementById("info_title").innerHTML = "New point added";
                document.getElementById("info_highlight").innerHTML = "";
                document.getElementById("info_div").style.display = 'flex';
            } else {
                document.getElementById("erro_alert_text").innerHTML = "Erro to create new point.Check data and try again."
                error_div_event("cadastro_div");
            }

        })
        .catch(error => {
            console.log(error);
            alert("Erro to connect, try again.");
        });
}