const config = {
    login_url: "http://localhost:8081/login",
    sign_in_url: "http://localhost:8081/signin"
}

let accessToken = null;

function afterLogin(){
    fechar_divs();

    const decode =  decodeURIComponent(atob(accessToken.split('.')[1].replace('-', '+').replace('_', '/')).split('').map(c => `%${('00' + c.charCodeAt(0).toString(16)).slice(-2)}`).join(''));
    console.log(decode);
    let nome = JSON.parse(decode)["nome_completo"]
    document.getElementById("welcome_name").innerHTML =  nome;
    welcome_div_show();
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
            accessToken = data.token;
            afterLogin();
        })
        .catch(error => {
           console.log(error);
           alert("Erro to login.");
        });

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
            accessToken = data.token;
            afterLogin();

        })
        .catch(error => {
            console.log(error);
            alert("Erro to connect, try again.");
        });

}