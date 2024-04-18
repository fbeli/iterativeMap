//var latitude = 38.702314954617066;
//var longitude = -9.249271785242797;
let latitude;
let longitude;
function fly_back(){
   read_cookies();
   if(firstTime){
       document.getElementById("starting").style.display="block";
   }
   if(longitude == 'undefined' || longitude == undefined ){
       latitude = getLatitude();
       longitude = getLongitude();
       if(timeoutLocation()){
           map.flyTo({center: [longitude, latitude], zoom: 14});
       }
   }else{
       map.flyTo({center: [longitude, latitude], zoom: 14});
       fechar_divs();
   }
    add_logo();
}


function fly_back_map(){
    //document.getElementsByClxassName("mapboxgl-ctrl mapboxgl-ctrl-group")[0].style.display="none";
    document.getElementsByClassName("mapboxgl-ctrl-geolocate")[0].click();

}

function timeoutLocation(){
    setTimeout(function(){
        if(latitude == undefined || longitude == undefined){
            alert("Não foi possível obter a sua localização.");
            fechar_divs();
            return false;
        }else{
            map.flyTo({center: [longitude, latitude], zoom: 14});
            fly_back_map();
            fechar_divs();
            return true;
        }
    }, 5000);

}
function getLatitude(){
    if('geolocation' in navigator){
        navigator.geolocation.getCurrentPosition( 
            function(position) {
                latitude =  position.coords.latitude;
               
            },
            function(error) {
                console.log(error);
            },
            {timeout:7000});
    }

        return latitude;
}
function getLongitude(){
    if('geolocation' in navigator){
        navigator.geolocation.getCurrentPosition(
            (position) => {
                longitude =  position.coords.longitude;
            },
            (error) => {
                console.log(error);
            } ,{timeout:7000});
        
        
    }
    return longitude;
}
function save_cookies(cookie_name, cookie_value){
    document.cookie = cookie_name+"="+cookie_value;
}
async function read_cookies(){

    let token = "token=";
    let name ="name=";
    let lat = "latitude=";
    let long = "longitude=";
    let access = "access="
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    let c;
    for(let i = 0; i <ca.length; i++) {
        c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            userName = c.substring(name.length, c.length);
            get_li_after_login(userName);
        }
        if (c.indexOf(token) == 0) {
            accessToken = c.substring(token.length, c.length);
            //testToken();
        }
        if (c.indexOf(lat) == 0) {
            latitude = c.substring(lat.length, c.length);
        }
        if (c.indexOf(long) == 0) {
            longitude = c.substring(long.length, c.length);
        }
        if (c.indexOf(access) == 0) {
            firstTime = false;
        }else{
            save_cookies(access, 1);
        }
    }
}


function get_li_after_login(nome){
    if(nome.indexOf(' ') >= 0){
        nome = nome.split(" ")[0];
        document.getElementById("li_login_a_link").innerHTML = nome ;
        document.getElementById("sidebar_login_div").setAttribute("onclick", "open_div_login()");
    }else{
        document.getElementById("li_login_a_link").innerHTML = "Login" ;
        document.getElementById("sidebar_login_div").setAttribute("onclick", "login()");
    }

}

