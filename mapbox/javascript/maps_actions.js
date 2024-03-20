

// The value for 'accessToken' begins with 'pk...'
mapboxgl.accessToken = 'pk.eyJ1IjoiZmJlbGkiLCJhIjoiY2xyM3N2Z2NxMW1zaDJpbXlmN2tydXRwaCJ9.IKxTu4ci2HIp_yOdWhn8Rg';
let point;

const map = new mapboxgl.Map({
    container: 'map',
    // Replace YOUR_STYLE_URL with your style URL.
    style: 'mapbox://styles/fbeli/clr407wxw018b01r5bt6oht1o',
    center: [ -9.135905, 38.709844],
    zoom: 16,
    hash: true,


});

map.on('load', () => {
    map.addSource('s_mapfile_pt', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_pt_.geojson'
    });
    map.addSource('s_mapfile_en', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_en_.geojson'
    });
    map.addSource('s_mapfile_lisboasecreta', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_lisboasecreta_.geojson'
    });


    map.addLayer({
        'id': 'l_mapFile_pt',
        'type': 'symbol',
        'layout': {
            'icon-image': 'pt',
            'icon-size': 0.2
        },
        'source': 's_mapfile_pt'
    });

    map.addLayer({
        'id': 'l_mapFile_en',
        'type': 'symbol',
        'layout': {
            'icon-image': 'en',
            'icon-size': 0.2
        },
        'source': 's_mapfile_en'
    });
    map.addLayer({
        'id': 'l_lisboa_secreta',
        'type': 'symbol',
        'layout': {
            'icon-image': 'lisboa_secreta',
            'icon-size': 0.2
        },
        'source': 's_mapfile_lisboasecreta'
    });

});
map.addControl(
    new MapboxGeocoder({
        accessToken: mapboxgl.accessToken,
        mapboxgl: mapboxgl
    }),"bottom-right"
);

map.addControl(new mapboxgl.GeolocateControl({
    positionOptions: {
        enableHighAccuracy: true
    },
    trackUserLocation: true,
    showUserHeading: true
}),"bottom-right");





/*
Add an event listener that runs
  when a user clicks on the map element.
*/
map.on('click', (event) => {
    // If the user clicked on one of your markers, get its information.
    const features = map.queryRenderedFeatures(event.point, {
        layers: ['l_mapFile_pt', 'l_mapFile_en','l_lisboa_secreta' ] // replace with your layer name

    });


    if (!features.length) {

        if(map.getZoom() > zoom_to_create_point && create_point){
            cadastro();
            point = event.lngLat;
            document.getElementById("cadastro_lng").value = "Longitude: "+point.lng;
            document.getElementById("cadastro_lat").value = "Latitude: "+point.lat;
        }

    } else {
        const feature = features[0];

        let insideHtml = `<h3>${feature.properties.title}</h3>`;
        if(feature.properties.audio !== undefined && feature.properties.audio.length > 2){
            if(navigator.userAgent.indexOf("iPhone") > -1){
                insideHtml += `<div><button class="button_play" id="play" onclick="play_on_safari('${feature.properties.audio}')">Play</button><button style="display: none" class="button_play" id="stop" onclick="stop_on_safari()">Stop</button></div>`;

            }else {
                insideHtml += `<div ><audio class="audio"  controls><source src="${feature.properties.audio}" type="audio/mpeg"/>Your browser does not support the audio element.</audio></div>`;
            }
        }
        insideHtml += `<p>Created by: ${feature.properties.user_name}.</p><br/>`;
        if( feature.properties.user_guide === 'true'){
            insideHtml += `<p id="sh_professional">${feature.properties.user_name} is professional guide, <a href="#" onclick="getUser('${feature.properties.user_id}')"> contact.</a></p><br/>`;
        }else{
            insideHtml += `<p id="sh_professional">${feature.properties.user_name} is part of out community. </p>`;
        }
        if(feature.properties.user_share === 'true' ) {
            insideHtml += `<p>${feature.properties.user_instagram}</a></p>`
        }
        if (feature.properties.photo != null)
            insideHtml += `<img style="width: 100%" src="${feature.properties.photo}" /><br/>`;
        insideHtml += `<p>${feature.properties.description}</p><br/>`;
        const popup = new mapboxgl.Popup({offset: [0, 0]})
            .setLngLat(feature.geometry.coordinates)
            .setHTML(
                insideHtml
            )
            .addTo(map);
    }
});

map.on('move',() => {
    zoom = map.getZoom();
    document.getElementById("inside_zoom").innerHTML=zoom;
})

class User{
    constructor(email, nome, instagram, photo, phone, share, guide) {
        this.email = email;
        this.name = nome;
        this.instagram = instagram;
        this.photo = photo;
        this.phone = phone;
        this.share = share;
        this.guide = true;
        if(guide !== undefined)
            this.guide = guide;

    }
}

async function getUser(id) {
    await fetch(config.get_user + id)
        .then(response => response.json())
        .then(data => {
        user = new User(data.email, data.name, data.instagram, data.photo, data.phone, data.share, data.guide);
        show_guide(user);

    });


}
function show_guide(user){
    let include = `${user.instagram}`;
    if(user.guide === 'true'){
        include = `${user.email} <br> ${user.phone}`
    }

    document.getElementById("sh_professional").innerHTML = include;
}