function aprovarPoint(url){
    fetch(url , {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .then(data => {
           console.log(data);
        })
        .catch(error => {
            console.log(error);
        });
}

async function getPoint(pointId){
    url = config.env + config.point+pointId;
    try{
        const response = await fetch(url , {
            method: "GET",
            headers: {
                "Content-Type": "application/json;charset=UTF-8",
                "Authorization": accessToken
            }
        });
            const data = await response.json();

                console.log("Data recebida: "+data);
                return new Feature(data.audio, data.description, data.photo, data.pointId, data.shortDescription, data.title,
                    data.user_guide, data.user_id, data.user_instagram, data.user_name, data.user_share, data.longitude, data.latitude);
            }
            catch(error){
                console.log(error);
                return null;
            }
    }
