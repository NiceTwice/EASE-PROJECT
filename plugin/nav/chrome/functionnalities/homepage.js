chrome.storage.local.get("settings", function (res) {
    res = res["settings"];
    chrome.tabs.query({
        active: true,
        currentWindow: true
    }, function (tabs) {
        var active = tabs[0].id;
        if (res.homepage == true) {
            chrome.tabs.update(active, {
                url: "https://ease.space"
            }, function () {});
        } else {
            // Set the URL to the Local-NTP (New Tab Page)
            chrome.tabs.update(active, {
                url: "chrome-search://local-ntp/local-ntp.html"
            }, function () {});
        }
    });
});
