package ru.fourgotten.VoxileWrapper;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import ru.fourgotten.VoxileSecurity.Data.MessagesWrapper.MessageWrappedGame;
import ru.fourgotten.VoxileSecurity.Hacks.HacksApplicator;
import ru.fourgotten.VoxileSecurity.WrappedGameStarter;
import ru.methuselah.authlib.GlobalReplacementList;

public class Tweaker implements ITweaker
{
	private static WrappedGameStarter instance;
	static
	{
		try
		{
			for(Method method : Class.forName(Wrapper.class.getCanonicalName(), true, ClassLoader.getSystemClassLoader()).getMethods())
				if(method.getReturnType().equals(WrappedGameStarter.class))
				{
					instance = (WrappedGameStarter)method.invoke(null, new Object[] {});
					System.out.println("Successfully found wrapper instance...");
					break;
				}
		} catch(ClassNotFoundException ex) {
			System.err.println(ex);
		} catch(IllegalAccessException ex) {
			System.err.println(ex);
		} catch(IllegalArgumentException ex) {
			System.err.println(ex);
		} catch(InvocationTargetException ex) {
			System.err.println(ex);
		} catch(NullPointerException ex) {
			System.err.println(ex);
		}
	}
	@Override
	public void acceptOptions(List<String> args, File gameDirectory, File assetsDirectory, String profile)
	{
	}
	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader)
	{
		System.out.println("Injecting Methuselah into minecraft client...");
		if(instance != null)
		{
			final MessageWrappedGame message = instance.getMessage();
			if(message != null)
			{
				HacksApplicator.process(message, classLoader);
				return;
			}
		}
		HacksApplicator.process(new GlobalReplacementList(), classLoader);
	}
	@Override
	public String getLaunchTarget()
	{
		return "net.minecraft.client.main.Main";
	}
	@Override
	public String[] getLaunchArguments()
	{
		return new String[] {};
	}
}
