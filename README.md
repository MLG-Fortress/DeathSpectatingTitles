# DeathSpectatingTitles
Addon for DeathSpectating - displays configurable title messages to death spectating player

Requires [DeathSpectating](https://github.com/MLG-Fortress/DeathSpectating) to be installed.

[Downloads](https://github.com/MLG-Fortress/DeathSpectatingTitles/releases)

[![DeathSpectatingTitles emonstration video](http://img.youtube.com/vi/cFgPumDLi1c/0.jpg)](https://www.youtube.com/watch?v=cFgPumDLi1c)

Default config:
```yml
titles:
- You died!
- Game over!
subTitles:
- Respawning in {0}
- 'Score: {1}'
- 'Score: {1}, Respawning in {0}'
```

{0} is the remaining seconds until respawn
{1} is the "score." As far as I'm aware, the "score" is `Player#getTotalExperience`. If I'm wrong, either let me know or PR the correct solution please, thank you!
