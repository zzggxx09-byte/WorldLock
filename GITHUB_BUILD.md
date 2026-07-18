## Збірка через GitHub Actions

У мене вже є `.github/workflows/build.yml` — він автоматично збере мод при
кожному push у гілку `main`, і покладе готовий jar як "artifact" (це видно на
вкладці "Actions" твого репозиторію після пушу).

Але для роботи `./gradlew` потрібен gradle-wrapper (`gradlew`, `gradlew.bat`,
`gradle/wrapper/gradle-wrapper.jar`) — це стандартні файли, які я не можу тут
згенерувати (потрібен інтернет і бінарний файл). Тому перед тим як заливати
на GitHub, зроби так:

1. Завантаж офіційний Forge MDK для 1.12.2 звідси:
   https://files.minecraftforge.net/net/minecraftforge/forge/index_1.12.2.html
   (обери версію `1.12.2-14.23.5.2860`, кнопка "Mdk").
2. Розпакуй завантажений zip в окрему папку.
3. З цієї офіційної MDK-папки скопіюй собі:
   - `gradlew`
   - `gradlew.bat`
   - всю папку `gradle/` (там і лежить `gradle-wrapper.jar`)
4. Поклади ці файли в корінь ЦЬОГО проєкту (поруч із `build.gradle`,
   `README.md` і т.д.) — тобто просто "накладаєш" їх на мою структуру,
   нічого свого не видаляючи.
5. Тепер структура проєкту повністю готова для GitHub.

## Заливка на GitHub

```
cd singleworldmod
git init
git add .
git commit -m "init"
git branch -M main
git remote add origin https://github.com/<твій-нік>/<назва-репо>.git
git push -u origin main
```

Після push зайди в репозиторій → вкладка **Actions** → там буде запущена
збірка. Коли вона позеленіє (успішна), відкрий її → внизу секція
**Artifacts** → там буде `singleworldmod-jar` — завантажуєш zip, а всередині
готовий `.jar` мода.

⚠️ Не забудь, що `template_world.zip` (твій світ) теж має бути в репозиторії
за шляхом `src/main/resources/assets/singleworldmod/template_world.zip` —
інакше збірка або пройде без нього, і мод при спробі створити світ видасть
помилку "Не знайдено template_world.zip". Якщо світ важкий (сотні МБ) —
GitHub має ліміт 100 МБ на файл у звичайному репозиторії, тоді знадобиться
Git LFS.
