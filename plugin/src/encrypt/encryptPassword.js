function encryptPassword(password, callback){
    var encrypt = new JSEncrypt();
    getLastKey(function(keyDatas){
        encrypt.setPublicKey(keyDatas.key);
        callback({password:encrypt.encrypt(password), keyDate:keyDatas.date})
    });
}

function getLastKey(callback){
    var req = new XMLHttpRequest();
    req.open('GET', /*'http://localhost:8080/HelloWorld/resources/publicEncryptionKeys.txt'  */ 'https://ease.space/resources/publicEncryptionKeys.txt', false);
    req.send(null);
    if(req.status == 200){
        var keys = req.responseText.split("\n");
        var lastKey = keys[keys.length-1];
        callback({date:lastKey.substring(0,6), key:lastKey.substring(7,lastKey.length)});
    }
}