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
package es.dagaren.gladiator.search;

import java.util.LinkedList;

import es.dagaren.gladiator.evaluation.Evaluator;
import es.dagaren.gladiator.representation.Movement;
import es.dagaren.gladiator.representation.Position;

/**
 * @author dagaren
 *
 */
public class Searcher implements Runnable
{
   protected int deepLimit;
   protected long timeLimit;
   protected long nodesLimit;
   
   protected long initTime = 0;
   
   protected long visitedNodes;
   protected long cutoffs;
   
   protected volatile boolean search;
   protected volatile boolean exit;
   
   protected int bestScore;
   protected int deep;
   
   protected LinkedList<Movement> principalVariation;
   
   protected Thread thread;
   
   protected Position position;
   
   protected Evaluator evaluator;
   
   protected final int DRAW_SCORE = 0;
   protected final int CHECKMATE_SCORE = -10000;
   
   protected final int MIN_RATING =  -10000 - 1;
   protected final int MAX_RATING =   10000 + 1;
   
   protected SearcherObserver observer;
   
   public Searcher()
   {
      deepLimit = 4;
      timeLimit = -1;
      nodesLimit = -1;
      
      principalVariation = new LinkedList<Movement>();
      
      evaluator = new Evaluator();
      
      exit = false;
      search = false;
      
      thread = new Thread(this);
      thread.start();
   }
   
   public void run()
   {
      searchLoop();
   }
   
   public synchronized void initSearch(Position position)
   {
      this.position = position.getCopy();
      
      search = true;
      
      notifyAll();
   }
   
   public synchronized void finish()
   {
      exit = true;
      search = false;
      notifyAll();
   }
   
   public synchronized void searchLoop()
   {
      while(!exit)
      {
         synchronized(this)
         {
            while(!search && !exit)
            {
               try
               {
                  wait();
               }
               catch(Exception ex)
               {}
            }
            
            if(exit == true)
               break;
            
            //Se limpia la variante principal
            principalVariation = new LinkedList<Movement>();
            
            //Se resetea los nodos recorridos
            visitedNodes = 0;
            
            //Se inicia el tiempo de búsqueda
            initTime = System.currentTimeMillis();
           
            //Se llama a la función de búsquda
            search();
            
            search = false;
            
            if(observer != null)
               observer.onSearchFinished(this);
         }
      }
   }
   
   protected void publishInfo()
   {
      long time = System.currentTimeMillis();
      
      SearchInfo info = new SearchInfo();
      info.time = (time - initTime) / 10;
      info.principalVariation = this.principalVariation;
      info.nodes = this.visitedNodes;
      info.deep = this.deep;
      info.score = this.bestScore;
      
      if(observer != null)
         observer.onInformationPublished(info);
   }
   
   public void search()
   {
      
   }
   
   public synchronized void stop()
   {
      search = false;
   }

   public int getDeepLimit()
   {
      return deepLimit;
   }

   public void setDeepLimit(int deepLimit)
   {
      this.deepLimit = deepLimit;
   }

   public long getTimeLimit()
   {
      return timeLimit;
   }

   public void setTimeLimit(long timeLimit)
   {
      this.timeLimit = timeLimit;
   }

   public long getNodesLimit()
   {
      return nodesLimit;
   }

   public void setNodesLimit(long nodesLimit)
   {
      this.nodesLimit = nodesLimit;
   }

   public long getCutoffs()
   {
      return cutoffs;
   }

   public void setCutoffs(long cutoffs)
   {
      this.cutoffs = cutoffs;
   }

   public int getBestScore()
   {
      return bestScore;
   }

   public void setBestScore(int bestScore)
   {
      this.bestScore = bestScore;
   }

   public int getDeep()
   {
      return deep;
   }

   public void setDeep(int deep)
   {
      this.deep = deep;
   }

   public LinkedList<Movement> getPrincipalVariation()
   {
      return principalVariation;
   }

   public void setPrincipalVariation(LinkedList<Movement> principalVariation)
   {
      this.principalVariation = principalVariation;
   }

   public Position getPosition()
   {
      return position;
   }

   public void setPosition(Position position)
   {
      this.position = position;
   }

   public Evaluator getEvaluator()
   {
      return evaluator;
   }

   public void setEvaluator(Evaluator evaluator)
   {
      this.evaluator = evaluator;
   }

   public SearcherObserver getObserver()
   {
      return observer;
   }

   public void setObserver(SearcherObserver observer)
   {
      this.observer = observer;
   }

   public long getVisitedNodes()
   {
      return visitedNodes;
   }
   
   
}
