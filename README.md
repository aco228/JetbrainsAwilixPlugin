# JetbrainsAwilixPlugin

Plugin for easy development when using node [awilix](https://www.npmjs.com/package/awilix)

## Requirements

- Your project must be node
- Root of your project must be *src*
- In *src* you must have `container.js` where you define all [awilix](https://www.npmjs.com/package/awilix) dependencies
- Structure of your project must be:

Example of expected project structure:

![image](https://user-images.githubusercontent.com/35331284/170844588-a6dae835-7229-43e1-9894-68e7b16cd214.png)


## How to use

When you select text in your jetbrains IDE, right click and you will have two options in context menu:

![image](https://user-images.githubusercontent.com/35331284/170844722-4562edb1-93bb-456c-a30e-7b932f9ad8a5.png)

 - `[Awilix] - Go to container definition` - Will try to find implementation of this service in `container.js` and automatically open file where that implementation is
 - `[Awilix] - Go to implementation` - Will open `container.js` where this service is registered.

You can also use shortcut `CTRL- ALT - B`. Beware that this shortcut is reserved in jetbrains, so you will have to remove this shortcut from its original usage, or you can define another shortcut for this action.

If your shortcut is configured properly, you can directly go to implementation of function inside of awilix service:

![ctrlAltB](https://user-images.githubusercontent.com/35331284/170844845-d86c857e-8cad-4ecc-a2e1-96ce59883a13.gif)

If your cursor is inside the name of function, when you perform shortcut action, it will try to get service from the `container.js` and if succede, it will try to locate function isnide service implementation.



## How to install from github

- Download latest version from (releases)[https://github.com/aco228/JetbrainsAwilixPlugin/releases/]
- In your Jetbrains go to `Plugins`
- Click on `Install plugin from disk` and use `.zip` you downloaded from github.

![image](https://user-images.githubusercontent.com/35331284/170844932-81aeed88-60d5-4e30-a4d8-64f4d26f34ff.png)



