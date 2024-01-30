

// The value for 'accessToken' begins with 'pk...'
mapboxgl.accessToken = 'pk.eyJ1IjoiZmJlbGkiLCJhIjoiY2xyM3N2Z2NxMW1zaDJpbXlmN2tydXRwaCJ9.IKxTu4ci2HIp_yOdWhn8Rg';

const map = new mapboxgl.Map({
    container: 'map',
    // Replace YOUR_STYLE_URL with your style URL.
    style: 'mapbox://styles/fbeli/clr407wxw018b01r5bt6oht1o',
    center: [  -9.22, 30.71],
    zoom: 10,
    hash: true,


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
        layers: ['chicago-rio-casa'] // replace with your layer name

    });


    if (!features.length) {

        if(map.getZoom() > zoom_to_create_point && create_point){
            cadastro();
            point = event.lngLat;
            document.getElementById("cadastro_lngLat").value = "longitude: "+point.lng+", Latitude"+point.lat;
        }

    } else {
        const feature = features[0];

        const popup = new mapboxgl.Popup({ offset: [0, -15] })
            .setLngLat(feature.geometry.coordinates)
            .setHTML(
                `<h3>${feature.properties.title}</h3><p>${feature.properties.description}</p>`
            )
            .addTo(map);
    }
});

map.on('move',() => {
    zoom = map.getZoom();
})

