//var latitude = 38.702314954617066;
//var longitude = -9.249271785242797;
let latitude;
let longitude;
function fly_back(){

   latitude = getLatitude();
   longitude = getLongitude();

   if(timeoutLocation()){
       map.flyTo({center: [longitude, latitude], zoom: 14});
   }

}

function timeoutLocation(){
    setTimeout(function(){
        if(latitude == undefined || longitude == undefined){
            alert("Não foi possível obter a sua localização.");
            return false;
        }else{
            alert("Peguei sua localização.");
            map.flyTo({center: [longitude, latitude], zoom: 14});
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

