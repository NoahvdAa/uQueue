# uQueue

<p align="center">
	<a href="https://www.codacy.com/gh/NoahvdAa/uQueue/dashboard?utm_source=github.com&utm_medium=referral&utm_content=NoahvdAa/uQueue&utm_campaign=Badge_Grade">
		<img src="https://app.codacy.com/project/badge/Grade/44544d06edf545ee921509834a595b1f">
		<img src="https://img.shields.io/github/last-commit/NoahvdAa/uQueue">
	</a>
</p>
<p align="center">
	<a href="https://bstats.org/plugin/bungeecord/uQueue/11230">
		<img src="https://img.shields.io/bstats/servers/11230">
		<img src="https://img.shields.io/bstats/players/11230">
	</a>
</p>

## Documentation

The documentation for this plugin can be found on my [wiki site](https://wiki.noah.pm/books/uqueue).

## Submodules

This project consists of multiple maven submodules:

- uqueue-api: The dependency to use when integrating uQueue into a plugin. For more information, go to the section **Developer API**.
- uqueue-plugin: The plugin to be installed on BungeeCord and Spigot servers.
- uqueue-ultra-permissions: The Ultra Permissions integration. For an explanation as to why this is seperated, check the module's [README](https://github.com/NoahvdAa/uQueue/blob/master/uqueue-ultra-permissions/README.md).

## Translations

Translated versions of messages.yml can be found in the [translations folder](https://github.com/NoahvdAa/uQueue/tree/master/translations). When creating a new translation, you should put your name right below the ASCII art, like this:

```yaml
#
# <ascii art>
# By NoahvdAa
#
# <language> Translation (In the language you're translating in)
# Translated by <your name> (In the language you're translating in)
#
```

## Developer API

uQueue has a developer API, for instructions on how to use it, check the [wiki page](https://wiki.noah.pm/books/uqueue/chapter/developer-api). The javadoc can be found on [Github Pages](https://noahvdaa.github.io/uQueue/javadocs/index.html).
