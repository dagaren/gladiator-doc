/*
 * Copyright (c) 2008-2011, David Garcinuño Enríquez <dagaren@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.dagaren.gladiator.communication;

import java.util.Scanner;

/**
 * @author dagaren
 *
 */
public class ConsoleCommandController extends CommandController implements Runnable
{
   boolean stopped = true;
   
   
   @Override
   public void sendCommand(String command) 
   {
      System.out.println(command);
   }

   @Override
   public void start()
   {
      if(stopped)
         new Thread(this).start();
   }

   @Override
   public void stop() 
   {
      stopped = false;
      
      //TODO falta hacer que se despierte el hilo en
      //caso de que esté dormido esperando una cadena
   }
   
   public void run()
   {
      stopped = false;
      Scanner input = new Scanner(System.in);
      
      while(!stopped)
      {
         String command = input.nextLine();
         
         for(CommandReceiver receiver : receivers)
         {
            receiver.onCommandReceived(command);
         }
      }
   }

}