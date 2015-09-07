# slimer: ТЕХДОК

## Платформа приложения

**Планшет**: cube U30GT-H

**ОС**: Android 4.0.4

## Описание работы приложения

Приложение будет иметь два состоятия:
- поиск беспроводного зарядного устройства
- зарядка планшета

### _Состояние 1_: поиск беспроводного зарядного устройства
    **Планшет**: включён.
    **Зарядка**: не заряжается.
    **Состояние приложения**: запущено.
    **Экран**:
        Задний план: изображение с фронтальной камеры (превью).
        Передний план: "метание" Лизуна по всему экрану.
        Заблокирован.

### _Состояние 2_: зарядка планшета
    **Планшет**: включён.
    **Зарядка**: заряжается.
    **Состояние приложения**: запущено.
    **Экран**:
            Задний план: изображение с фронтальной камеры (превью).
            Передний план: Лизун, находясь в центре экрана, указывает на верхний левый угол.
            Заблокирован.

При запуске приложения планшет входит в _состояние 1_ (Лизун мечется по экрану, как в клетке).
Возможна стартовая анимация появления лизуна.

При постановке планшета на беспроводную зарядку, он переходит в _состояние 2_ (Лизун указывает на верхний левый угол планшета).
При отключении планшета от беспроводной зарядки планшет переходит в _состояние 1_ (Лизун снова ничинает метаться по экрану).

При нажатии определенной комбинации в течении определенного времени приложение закрывается.

## Изображения

[[http://google.com][**лизун:**]]

[[http://google.com][**лизун-состояние_2:**]]