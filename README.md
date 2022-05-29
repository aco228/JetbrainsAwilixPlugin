# JetbrainsAwilixPlugin

Plugin for easy development when using node [awilix](https://www.npmjs.com/package/awilix)

## Requirements

- Your project must be node
- Root of your project will be directory where `package.json` is located
- In your project root you must have `container.js` where you define all [awilix](https://www.npmjs.com/package/awilix) dependencies

## How to use

When you select text in your jetbrains IDE, right click and you will have two options in context menu:

![image](https://user-images.githubusercontent.com/35331284/170844722-4562edb1-93bb-456c-a30e-7b932f9ad8a5.png)

 - `[Awilix] - Go to container definition` - Will try to find implementation of this service in `container.js` and automatically open file where that implementation is
 - `[Awilix] - Go to implementation` - Will open `container.js` where this service is registered.

You can also use shortcut `CTRL- ALT - B`. Beware that this shortcut is reserved in jetbrains, so you will have to remove this shortcut from its original usage, or you can define another shortcut for this action.

If your shortcut is configured properly, you can directly go to implementation of function inside of awilix service:

![ctrlAltB](https://user-images.githubusercontent.com/35331284/170844845-d86c857e-8cad-4ecc-a2e1-96ce59883a13.gif)

If your cursor is inside the name of function, when you perform shortcut action, it will try to get service from the `container.js` and if succede, it will try to locate function isnide service implementation.


## Shortcut

Defined shortcut for this plugin is `Ctrl Alt B`, just so it can be similar to `Ctrl B` (default jetbrains shortcut for go to definition). But this shortcut is already defined by default in jetbrains, so in order to use it, you will need to remove it its default usage, or define new one

![image](https://user-images.githubusercontent.com/35331284/170890426-af84f213-5dc6-43f9-9cca-095343fb1d7e.png)

## How to install from github

- Download latest version from (releases)[https://github.com/aco228/JetbrainsAwilixPlugin/releases/]
- In your Jetbrains go to `Plugins`
- Click on `Install plugin from disk` and use `.zip` you downloaded from github.

![image](https://user-images.githubusercontent.com/35331284/170844932-81aeed88-60d5-4e30-a4d8-64f4d26f34ff.png)



