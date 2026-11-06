# vratim

Dynamic Kotlin application for forwarding and obtaining the current task status.

## Architecture Features
* **Zero-Allocation & Zero-Copy:** Network communication and parsing are optimized for primitive types, eliminating excessive load on the heap and the Android garbage collector.
* **Fully Data-Driven:** The application code completely lacks hardcode of entities and localization strings. The entire interface, drop-down lists and language packs are built dynamically on the fly from a local configuration file.

## Config structure

The application stores its configuration in internal isolated storage. At the first launch, a working template is automatically generated. 

You can completely update the configuration, by copying JSON to the smartphone clipboard and selecting **"Paste JSON"** in the settings menu.

### Contract restrictions
1. The names of all fields must be strictly in **lowercase**.
2. Maximum number of elements in an array `entities` — **256**.
3. The numeric ranges for `id` and `status` are strictly within **0..255**.
4. Если у сущности в массиве отсутствует поле `id`, рантайм автоматически подставит её порядковый индекс в массиве.

### Example config:
```json
{
  "url": "https://yourhost",
  "get": "/api/v1/pull/client",
  "post": "/api/v1/push",
  "lang": "en",
  "entities": [
    {
      "name": "Room lighting",
      "id": 1,
      "texts": [
        { "status": 0, "text": "Off" },
        { "status": 1, "text": "Included" }
      ]
    },
    {
      "name": "System trigger",
      "texts": [
        { "status": 12, "text": "Request logs" }
      ]
    }
  ],
  "langs": [
    {
      "lang": "en",
      "title": "Control Panel",
      "btn_send": "Send",
      "btn_back": "Back",
      "btn_save": "Save changes",
      "menu_send_status": "Send status",
      "menu_global": "Global Settings",
      "menu_paste": "Paste JSON",
      "menu_refresh": "Update status",
      "entity_label": "Target"
    }
  ]
}
```

## Deployment instructions for users
1. **Настройте сервер:** Run your Rust server locally on your PC and forward it to the network via Loophole, receiving a public HTTPS link.
2. **Скачайте клиент:** Go to the [**Releases**](../../releases) tab and download the current `vratim.apk` file.
3. **Установите на телефон:** Transfer the APK to your real Android smartphone and install (allowing installation from unknown sources in security settings).
4. **Примените конфигурацию:** 
   * Generate your personal JSON config.
   * Copy the resulting JSON text in its entirety.
   * Open the app on your smartphone, click on the gear icon in the top right corner and select **"Paste JSON"**.
5. The system will instantly rebuild, upgrade the language pack and make the first network poll of the server.
