//var latitude = 38.702314954617066;
//var longitude = -9.249271785242797;

var defaultZoom = 14.03;
async function fly_back() {
    read_cookies();

    readStartPageVariables().then( result =>   {
    if(result){
        map.flyTo({center: [received_long, received_lat], zoom: received_zoom});
    }
    else {
        if (firstTime) {
            document.getElementById("starting").style.display = "block";
        }
        if (window.location.href.search(defaultZoom) != -1) {
            if (latitude == 'undefined' || longitude == 'undefined' || latitude == null || longitude == null) {
                latitude = getLatitude();
                longitude = getLongitude();
                if (timeoutLocation()) {
                    map.flyTo({center: [longitude, latitude], zoom: defaultZoom});
                }
            } else {
                map.flyTo({center: [longitude, latitude], zoom: defaultZoom});
                fechar_divs();
            }
        }
        add_logo();
    }});
}

    function fly_back_map() {
        //document.getElementsByClxassName("mapboxgl-ctrl mapboxgl-ctrl-group")[0].style.display="none";
        document.getElementsByClassName("mapboxgl-ctrl-geolocate")[0].click();

    }

    function timeoutLocation() {
        setTimeout(function () {
            if (latitude == 'undefined' || longitude == 'undefined' || latitude == null || longitude == null) {
                console.log("Não foi possível obter a sua localização.");
                map.flyTo({center: [long_start_lisbon, lat_start_lisbon], zoom: defaultZoom});
                fechar_divs();
                return false;
            } else {
                map.flyTo({center: [longitude, latitude], zoom: defaultZoom});
                fly_back_map();
                fechar_divs();
                return true;
            }
        }, 5000);

    }

    function getLatitude() {
        if ('geolocation' in navigator) {
            navigator.geolocation.getCurrentPosition(
                function (position) {
                    latitude = position.coords.latitude;

                },
                function (error) {
                    console.log(error);
                },
                {timeout: 7000});
            save_cookies("latitude", latitude);
        } else {
            if (latitude == 'undefined') {
                return lat_start_lisbon;
            }
        }
        return latitude;
    }

    function getLongitude() {
        if ('geolocation' in navigator) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    longitude = position.coords.longitude;
                },
                (error) => {
                    console.log(error);
                }, {timeout: 7000});
            save_cookies("longitude", longitude);
        } else {
            if (longitude == 'undefined') {
                return long_start_lisbon;
            }
        }
        return longitude;
    }

    function save_cookies(cookie_name, cookie_value) {
        document.cookie = cookie_name + "=" + cookie_value;
    }

    function get_li_after_login(nome) {
        if (nome.indexOf(' ') >= 0) {
            nome = nome.split(" ")[0];
        }
        if (nome != null && nome.length > 0 && document.getElementById("li_login_a_link") != null
            && document.getElementById("sidebar_login_div") != null) {
            document.getElementById("li_login_a_link").innerHTML = nome;
            document.getElementById("sidebar_login_div").setAttribute("onclick", "open_div_login()");
        }

    }

    read_cookies();



