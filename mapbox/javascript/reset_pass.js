
let previous_div = null;

function validate_password(){
    let password =  document.getElementById("reset_password").value;
    let second_password = document.getElementById("repeat_reset_password").value;
    if(password.length < 8) {
        document.getElementById("reset_response").innerHTML = "Password must be at least 8 characters long";
        return false;
    }if(password !== second_password){
        document.getElementById("reset_response").innerHTML = "passwords are different";
        return false;
    }
    return true;

}
async function reset_password() {
    if(!validate_password()){
        return false;
    }
    let data = {
        email: document.getElementById("reset_email").value,
        password: document.getElementById("reset_password").value,
        code: document.getElementById("reset_code").value
    };
    fetch(config.reset_url, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            if (data.status == "200") {
                document.getElementById("reset_response").innerHTML = "New Password saved.";
            } else {
                document.getElementById("reset_response").innerHTML = "Error to set new password.<br>"+data.error;
            }
        })
        .catch(error => {
            console.log(error);
            document.getElementById("reset_response").innerHTML = "Error to set new password.";
        });
}
