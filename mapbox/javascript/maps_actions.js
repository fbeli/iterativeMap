

// The value for 'accessToken' begins with 'pk...'
mapboxgl.accessToken = 'pk.eyJ1IjoiZmJlbGkiLCJhIjoiY2xyM3N2Z2NxMW1zaDJpbXlmN2tydXRwaCJ9.IKxTu4ci2HIp_yOdWhn8Rg';

const map = new mapboxgl.Map({
    container: 'map',
    // Replace YOUR_STYLE_URL with your style URL.
    style: 'mapbox://styles/fbeli/clr407wxw018b01r5bt6oht1o',
    center: [ 38.69162, -9.2158],
    zoom: 10,
    hash: true,


});

map.on('load', () => {
    map.addSource('earthquakes', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_.geojson'
    });

    map.addLayer({
        'id': 'WorldTurism2',
        'type': 'symbol',
        'layout': {
            'icon-image': 'marker-editor',
            'icon-size': 1
        },
        'source': 'earthquakes'

    });

});

map.addControl(new mapboxgl.GeolocateControl({
    positionOptions: {
        enableHighAccuracy: true
    },
    trackUserLocation: true,
    showUserHeading: true
}),"bottom-right");

var point;

/*
Add an event listener that runs
  when a user clicks on the map element.
*/
map.on('click', (event) => {
    // If the user clicked on one of your markers, get its information.
    const features = map.queryRenderedFeatures(event.point, {
        layers: ['WorldTurism', 'WorldTurism2' ] // replace with your layer name

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
        insideHtml += `<p>${feature.properties.description}</p><br/>`;
        const popup = new mapboxgl.Popup({offset: [0, -15]})
            .setLngLat(feature.geometry.coordinates)
            .setHTML(
                insideHtml
            )
            .addTo(map);
    }
});

map.on('move',() => {
    zoom = map.getZoom();
})

